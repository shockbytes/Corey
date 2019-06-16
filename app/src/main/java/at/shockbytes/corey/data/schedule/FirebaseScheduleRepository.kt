package at.shockbytes.corey.data.schedule

import android.content.Context
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.data.workout.WorkoutRepository
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

    init {
        setupFirebase()
    }

    private val scheduleItems: MutableList<ScheduleItem> = mutableListOf()

    private val scheduleItemSubject = BehaviorSubject.create<List<ScheduleItem>>()
    override val schedule: Observable<List<ScheduleItem>> = scheduleItemSubject

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

    override fun poke() = Unit

    override fun insertScheduleItem(item: ScheduleItem): ScheduleItem {
        val ref = firebase.getReference("/schedule").push()
        val updated = item.copy(id = ref.key ?: "")
        ref.setValue(updated)
        return updated
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).setValue(item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).removeValue()
    }

    override fun deleteAll(): Completable {
        return Completable
                .create { emitter ->
                    firebase.getReference("/schedule").removeValue()
                            .addOnCompleteListener { emitter.onComplete() }
                            .addOnFailureListener { throwable -> emitter.onError(throwable) }
                }
                .subscribeOn(schedulers.io)
    }

    private fun setupFirebase() {

        firebase.getReference("/schedule").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.getValue(ScheduleItem::class.java)?.let { item ->
                    scheduleItems.add(item)
                    scheduleItemSubject.onNext(scheduleItems)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                dataSnapshot.getValue(ScheduleItem::class.java)?.let { changed ->
                    scheduleItems[scheduleItems.indexOf(changed)] = changed
                    scheduleItemSubject.onNext(scheduleItems)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(ScheduleItem::class.java)?.let { removed ->
                    scheduleItems.remove(removed)
                    scheduleItemSubject.onNext(scheduleItems)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                Timber.d("ScheduleItem moved: $dataSnapshot / $s")
            }

            override fun onCancelled(databaseError: DatabaseError) = Unit
        })
    }
}
