package at.shockbytes.corey.data.nutrition

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.util.*
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseNutritionRepository(
        private val firebase: FirebaseDatabase,
        private val schedulers: SchedulerFacade,
) : NutritionRepository {

    private val nutritionSubject = BehaviorSubject.create<List<NutritionEntry>>() //.createDefault<List<NutritionEntry>>(listOf())

    init {
        setupFirebase()
    }

    private fun setupFirebase() {
        firebase.listen(REF, nutritionSubject)
    }

    override fun loadDailyNutritionEntries(): Observable<List<NutritionPerDay>> {
        return nutritionSubject
                .map { entries ->
                    entries
                            .groupBy { it.date }
                            .map { (date, nutritionEntries) ->
                                NutritionPerDay(
                                        intake = nutritionEntries,
                                        date = date,
                                        burned = listOf() // TODO Where to get this...
                                )
                            }
                            .sortedByDescending { it.date.dateTime }
                }
                .subscribeOn(schedulers.io)
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