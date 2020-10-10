package at.shockbytes.corey.data.schedule

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.util.*
import com.google.firebase.database.FirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    22.02.2017
 */
class FirebaseScheduleRepository(
    private val firebase: FirebaseDatabase,
    private val schedulers: SchedulerFacade,
    private val schedulableItemResolver: SchedulableItemResolver
) : ScheduleRepository {

    private val scheduleItemSubject = BehaviorSubject.create<List<ScheduleItem>>()
    override val schedule: Observable<List<ScheduleItem>> = scheduleItemSubject

    override val schedulableItems: Observable<List<SchedulableItem>>
        get() = schedulableItemResolver.resolveSchedulableItems()

    init {
        setupFirebase()
    }

    override fun insertScheduleItem(item: ScheduleItem): ScheduleItem {
        return firebase.insertValue(REF_SCHEDULE, item)
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        firebase.updateValue(REF_SCHEDULE, item.id, item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        firebase.removeValue(REF_SCHEDULE, item.id)
    }

    override fun deleteAll(): Completable {
        return Completable
                .create { emitter ->
                    firebase.getReference(REF_SCHEDULE).removeValue()
                            .addOnCompleteListener { emitter.onComplete() }
                            .addOnFailureListener { throwable -> emitter.onError(throwable) }
                }
                .subscribeOn(schedulers.io)
    }

    private fun setupFirebase() {
        scheduleItemSubject.fromFirebase(
                dbRef = firebase.getReference(REF_SCHEDULE),
                changedChildKeySelector = { it.id },
                cancelHandler = { dbError -> Timber.e(dbError.toException()) }
        )
    }

    companion object {
        private const val REF_SCHEDULE = "/schedule"
    }
}
