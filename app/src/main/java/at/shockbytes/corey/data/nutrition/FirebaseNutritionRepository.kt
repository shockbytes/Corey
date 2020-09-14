package at.shockbytes.corey.data.nutrition

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.workout.external.ExternalWorkout
import at.shockbytes.corey.data.workout.external.ExternalWorkoutRepository
import at.shockbytes.corey.util.*
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseNutritionRepository(
        private val firebase: FirebaseDatabase,
        private val schedulers: SchedulerFacade,
        private val externalWorkoutRepository: ExternalWorkoutRepository
) : NutritionRepository {

    private val nutritionSubject = BehaviorSubject.create<List<NutritionEntry>>() //.createDefault<List<NutritionEntry>>(listOf())

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        firebase.listen(REF, nutritionSubject)
    }

    override fun loadDailyNutritionEntries(): Observable<List<NutritionPerDay>> {
        return buildEnergyBalanceObservable()
                .map(::energyBalanceToNutritionPerDayItems)
                .subscribeOn(schedulers.io)
    }

    private data class EnergyBalance(
            val nutritionPerDay: List<NutritionEntry>,
            val externalWorkouts: List<ExternalWorkout>,
    )

    private fun buildEnergyBalanceObservable(): Observable<EnergyBalance> {
        return Observable
                .zip(
                        nutritionSubject,
                        externalWorkoutRepository.loadExternalWorkouts(),
                        { nutritionPerDay, externalWorkouts ->
                            EnergyBalance(nutritionPerDay, externalWorkouts)
                        }
                )
    }

    private fun energyBalanceToNutritionPerDayItems(energyBalance: EnergyBalance): List<NutritionPerDay> {
        return energyBalance.nutritionPerDay
                .groupBy { it.date }
                .map { (date, nutritionEntries) ->
                    NutritionPerDay(
                            intake = nutritionEntries,
                            date = date,
                            // TODO Replace this with the proper calls
                            burned = listOf(
                                    // PhysicalActivity.BasalMetabolicRate("Basal Metabolic Rate", 1700),
                                    // PhysicalActivity.Activity("Freeletics", 900)
                            )
                    )
                }
                .sortedByDescending { it.date.dateTime }
    }

    override fun addNutritionEntry(entry: NutritionEntry): Completable {
        return completableEmitterOf {
            firebase.insertValue(REF, entry)
        }
    }

    override fun deleteNutritionEntry(id: String): Completable {
        return Completable.fromAction {
            firebase.removeValue(REF, id)
        }
    }

    companion object {
        private const val REF = "/nutrition"
    }
}