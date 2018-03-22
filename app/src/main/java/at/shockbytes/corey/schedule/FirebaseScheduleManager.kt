package at.shockbytes.corey.schedule

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.util.Log
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.core.receiver.NotificationReceiver
import at.shockbytes.corey.util.CoreyAppUtils
import at.shockbytes.corey.workout.WorkoutManager
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author  Martin Macheiner
 * Date:    22.02.2017
 */

class FirebaseScheduleManager(private val context: Context,
                              private val preferences: SharedPreferences,
                              private val gson: Gson,
                              private val workoutManager: WorkoutManager,
                              private val remoteConfig: FirebaseRemoteConfig,
                              private val firebase: FirebaseDatabase) : ScheduleManager {

    init {
        setupFirebase()
    }

    private val scheduleItems: MutableList<ScheduleItem> = mutableListOf()
    private var scheduleListener: LiveScheduleUpdateListener? = null

    override val schedule: Observable<List<ScheduleItem>>
        get() = Observable.just(scheduleItems.toList())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override val itemsForScheduling: Observable<List<String>>
        get() = Observable.fromCallable {
            val items = mutableListOf<String>()
            workoutManager.workouts.blockingFirst().mapTo(items) { it.displayableName }

            val schedulingItemsAsJson = remoteConfig
                    .getString(context.getString(R.string.remote_config_scheduling_items))
            val remoteConfigItems = gson.fromJson(schedulingItemsAsJson, Array<String>::class.java)
            items.addAll(remoteConfigItems)

            items.toList()
        }.observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())

    override val isWorkoutNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_workout_day_notification_key), false)

    override val isWeighNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_weigh_notification_key), false)

    override val dayOfWeighNotificationDelivery: Int
        get() = preferences.getString(context.getString(R.string.prefs_weigh_notification_day_key),
                context.getString(R.string.prefs_weigh_notification_day_default_value)).toInt()

    override fun poke(activity: FragmentActivity?) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(context, 0x9238, intent, 0)

        val (hour, minute) = readNotificationTimeFromPreferences()
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        //Add a day if alarm is set for before current timeStamp, so the alarm is triggered the next day
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
        item.id = ref.key
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

    override fun registerLiveScheduleUpdates(listener: LiveScheduleUpdateListener) {
        this.scheduleListener = listener
    }

    override fun unregisterLiveScheduleUpdates() {
        scheduleListener = null
    }

    private fun readNotificationTimeFromPreferences(): List<Int> {
        val str = preferences.getString(context.getString(R.string.prefs_workout_day_notification_daytime_key),
                context.getString(R.string.prefs_workout_day_notification_daytime_defValue))
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

                val item = dataSnapshot.getValue(ScheduleItem::class.java)
                if (item != null) {
                    scheduleItems.add(item)
                    scheduleListener?.onScheduleItemAdded(item)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                val changed = dataSnapshot.getValue(ScheduleItem::class.java)
                if (changed != null) {
                    scheduleItems[scheduleItems.indexOf(changed)] = changed
                    scheduleListener?.onScheduleItemChanged(changed)
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

                val removed = dataSnapshot.getValue(ScheduleItem::class.java)
                if (removed != null) {
                    scheduleItems.remove(removed)
                    scheduleListener?.onScheduleItemDeleted(removed)
                }
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String) {
                Log.wtf("Corey", "ScheduleItem moved: " + dataSnapshot.toString() + " / " + s)
            }

            override fun onCancelled(databaseError: DatabaseError?) {
            }
        })
    }

}
