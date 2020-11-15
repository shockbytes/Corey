package at.shockbytes.corey.data.schedule

import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.data.firebase.FirebaseDatabaseAccess
import at.shockbytes.corey.util.fromFirebase
import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.reactiveRemoveValue
import at.shockbytes.corey.util.removeChildValue
import at.shockbytes.corey.util.updateValue
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    22.02.2017
 */
class FirebaseScheduleRepository(
    private val firebase: FirebaseDatabaseAccess,
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

    private fun setupFirebase() {
        scheduleItemSubject.fromFirebase(
                dbRef = firebase.access(REF_SCHEDULE),
                changedChildKeySelector = { it.id },
                cancelHandler = { dbError -> Timber.e(dbError.toException()) }
        )
    }

    override fun insertScheduleItem(item: ScheduleItem): ScheduleItem {
        return firebase.access(REF_SCHEDULE).insertValue(item)
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        firebase.access(REF_SCHEDULE).updateValue(item.id, item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        firebase.access(REF_SCHEDULE).removeChildValue(item.id)
    }

    override fun deleteAll(): Completable {
        return firebase.access(REF_SCHEDULE).reactiveRemoveValue()
                .subscribeOn(schedulers.io)
    }

    companion object {
        private const val REF_SCHEDULE = "/schedule"
    }
}
