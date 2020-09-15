package at.shockbytes.corey.data.nutrition

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.CoreyDate
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.bmr.BmrComputation
import at.shockbytes.corey.data.body.model.User
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
        private val externalWorkoutRepository: ExternalWorkoutRepository,
        private val bodyRepository: BodyRepository,
        private val bmrComputation: BmrComputation
) : NutritionRepository {

    private val nutritionSubject = BehaviorSubject.create<List<NutritionEntry>>()

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        firebase.listen(REF, nutritionSubject, changedChildKeySelector = { it.id })
    }

    override val bmrComputationName: String
        get() = bmrComputation.name

    override fun loadDailyNutritionEntries(): Observable<List<NutritionPerDay>> {
        return buildEnergyBalanceObservable()
                .map(::energyBalanceToNutritionPerDayItems)
                .subscribeOn(schedulers.io)
    }

    private data class EnergyBalance(
            val nutritionPerDay: List<NutritionEntry>,
            val externalWorkouts: List<ExternalWorkout>,
            val user: User
    )

    private fun buildEnergyBalanceObservable(): Observable<EnergyBalance> {
        return Observable
                .combineLatest(
                        nutritionSubject,
                        externalWorkoutRepository.loadExternalWorkouts(),
                        bodyRepository.user,
                        { nutritionPerDay, externalWorkouts, user ->
                            EnergyBalance(nutritionPerDay, externalWorkouts, user)
                        }
                )
    }

    private fun energyBalanceToNutritionPerDayItems(
            energyBalance: EnergyBalance
    ): List<NutritionPerDay> {
        val (nutrition, externalWorkouts, user) = energyBalance

        return nutrition
                .groupBy { it.date }
                .map { (date, nutritionEntries) ->
                    NutritionPerDay(
                            intake = nutritionEntries,
                            date = date,
                            burned = computePhysicalActivityOfDate(date, user, externalWorkouts)
                    )
                }
                .sortedByDescending { it.date.dateTime }
    }

    private fun computePhysicalActivityOfDate(
            date: CoreyDate,
            user: User,
            externalWorkouts: List<ExternalWorkout>
    ): List<PhysicalActivity> {
        return listOf(computeBmr(date, user)) + computeExternalActivity(date, externalWorkouts)
    }

    private fun computeBmr(date: CoreyDate, user: User): PhysicalActivity.BasalMetabolicRate {

        val userWeightOfDate = user.retrieveUserWeightAt(date)
        val userAgeOfDate = user.age // TODO not so important that this changes over time

        return bmrComputation
                .compute(
                        user.gender,
                        userWeightOfDate,
                        user.height,
                        userAgeOfDate,
                        user.activityLevel
                )
                .let(PhysicalActivity::BasalMetabolicRate)
    }

    private fun User.retrieveUserWeightAt(date: CoreyDate): Double {

        val currentWeight = latestWeightDataPoint
        return if (currentWeight != null) {
            weightDataPoints.findClosest(date, currentWeight).weight
        } else 0.0
    }

    private fun computeExternalActivity(
            date: CoreyDate,
            externalWorkouts: List<ExternalWorkout>
    ): List<PhysicalActivity.Activity> {
        return externalWorkouts
                .filter { it.date == date }
                .map { externalWorkout ->
                    PhysicalActivity.Activity(externalWorkout.name, externalWorkout.burnedEnergy)
                }
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