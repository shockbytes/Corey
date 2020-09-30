package at.shockbytes.corey.data.schedule

import android.content.Context
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.data.workout.WorkoutRepository
import at.shockbytes.corey.util.insertValue
import at.shockbytes.corey.util.listen
import at.shockbytes.corey.util.removeValue
import at.shockbytes.corey.util.updateValue
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber

/**
 * Author:  Martin Macheiner
 * Date:    22.02.2017
 */
class FirebaseScheduleRepository(
    private val context: Context,
    private val gson: Gson,
    private val workoutManager: WorkoutRepository,
    private val remoteConfig: FirebaseRemoteConfig,
    private val firebase: FirebaseDatabase,
    private val schedulers: SchedulerFacade
) : ScheduleRepository {

    private val scheduleItemSubject = BehaviorSubject.create<List<ScheduleItem>>()
    override val schedule: Observable<List<ScheduleItem>> = scheduleItemSubject

    init {
        setupFirebase()
    }

    override val schedulableItems: Observable<List<SchedulableItem>>
        get() = workoutManager.workouts
                .map { workouts ->
                    val workoutItems = workouts
                            .map { w ->
                                SchedulableItem(
                                    w.displayableName,
                                    w.locationType,
                                    WorkoutIconType.fromBodyRegion(w.bodyRegion)
                                )
                            }
                            .toMutableList()

                    val schedulingItemsAsJson = remoteConfig
                            .getString(context.getString(R.string.remote_config_scheduling_items))
                    val remoteConfigItems = gson.fromJson(schedulingItemsAsJson, Array<SchedulableItem>::class.java)
                    workoutItems
                            .apply {
                                addAll(remoteConfigItems)
                            }
                            .toList()
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
        firebase.listen(REF_SCHEDULE, scheduleItemSubject, changedChildKeySelector = { it.id })
    }

    companion object {
        private const val REF_SCHEDULE = "/schedule"
    }
}
