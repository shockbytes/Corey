package at.shockbytes.corey.data.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.workout.model.WorkoutIconType
import at.shockbytes.corey.core.receiver.NotificationReceiver
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
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Author:  Martin Macheiner
 * Date:    22.02.2017
 */
class FirebaseScheduleRepository(
    private val context: Context,
    private val preferences: SharedPreferences,
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

    override fun poke() {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, 0x9238, intent, 0)

        val (hour, minute) = readNotificationTimeFromPreferences()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        // Add a day if alarm is set for before current timeStamp, so the alarm is triggered the next day
        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        // PendingIntent is null if already set http://stackoverflow.com/a/9575569/3111388
        if (pIntent != null) {
            // Set to fire every day
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis,
                    TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS), pIntent)
        }
    }

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

    private fun readNotificationTimeFromPreferences(): List<Int> {
        val str = preferences.getString(context.getString(R.string.prefs_workout_day_notification_daytime_key),
                context.getString(R.string.prefs_workout_day_notification_daytime_defValue)) ?: ""
        return str.split(":")
                .mapTo(mutableListOf()) { it.toInt() }
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
