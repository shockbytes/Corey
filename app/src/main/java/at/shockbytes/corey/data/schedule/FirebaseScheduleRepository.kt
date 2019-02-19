package at.shockbytes.corey.data.schedule

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.core.receiver.NotificationReceiver
import at.shockbytes.corey.util.CoreyAppUtils
import at.shockbytes.corey.data.workout.WorkoutRepository
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
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
    private val schedulerFacade: SchedulerFacade
) : ScheduleRepository {

    init {
        setupFirebase()
    }

    private val scheduleItems: MutableList<ScheduleItem> = mutableListOf()

    private val scheduleItemSubject = BehaviorSubject.create<List<ScheduleItem>>()
    override val schedule: Observable<List<ScheduleItem>> = scheduleItemSubject

    override val schedulableItems: Single<List<String>>
        get() = Single
                .fromCallable {
                    val items = mutableListOf<String>()
                    workoutManager.workouts.blockingFirst().mapTo(items) { it.displayableName }

                    val schedulingItemsAsJson = remoteConfig
                            .getString(context.getString(R.string.remote_config_scheduling_items))
                    val remoteConfigItems = gson.fromJson(schedulingItemsAsJson, Array<String>::class.java)
                    items.addAll(remoteConfigItems)

                    items.toList()
                }
                .subscribeOn(schedulerFacade.io)
                .observeOn(schedulerFacade.ui)

    override val isWorkoutNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_workout_day_notification_key), false)

    override val isWeighNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_weigh_notification_key), false)

    override val dayOfWeighNotificationDelivery: Int
        get() {
            return (preferences.getString(context.getString(R.string.prefs_weigh_notification_day_key),
                    context.getString(R.string.prefs_weigh_notification_day_default_value)) ?: "0").toInt()
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
        item.id = ref.key ?: ""
        ref.setValue(item)
        return item
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).setValue(item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        firebase.getReference("/schedule").child(item.id).removeValue()
    }

    override fun postWeighNotification() {
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x90, CoreyAppUtils.getWeighNotification(context))
    }

    override fun postWorkoutNotification() {
        schedule.subscribe({ scheduleItems ->
            scheduleItems
                    .firstOrNull { it.day == CoreyUtils.getDayOfWeek() && !it.isEmpty }
                    ?.let { item -> postWorkoutNotificationForToday(item) }
        }, { throwable ->
            Log.wtf("Corey", "Cannot retrieve workouts: ${throwable.localizedMessage}")
            Crashlytics.logException(throwable)
            Crashlytics.log(10, "Corey",
                    "Cannot retrieve workouts in postWorkoutNotification(): ${throwable.localizedMessage}")
        })
    }

    private fun readNotificationTimeFromPreferences(): List<Int> {
        val str = preferences.getString(context.getString(R.string.prefs_workout_day_notification_daytime_key),
                context.getString(R.string.prefs_workout_day_notification_daytime_defValue)) ?: ""
        return str.split(":")
                .mapTo(mutableListOf()) { it.toInt() }
    }

    private fun postWorkoutNotificationForToday(item: ScheduleItem) {
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x91, CoreyAppUtils.getWorkoutNotification(context, item.name))
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
