package at.shockbytes.corey.data.nutrition

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.util.completableOf
import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.listen
import at.shockbytes.corey.util.removeValue
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class FirebaseNutritionRepository(
        private val firebase: FirebaseDatabase,
        private val schedulers: SchedulerFacade,
) : NutritionRepository {

    init {
        setupFirebase()
    }

    private val nutritionSubject = BehaviorSubject.createDefault<List<NutritionEntry>>(listOf())

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
                }
                .subscribeOn(schedulers.io)
    }

    override fun addNutritionEntry(entry: NutritionEntry): Completable {
        return completableOf{
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