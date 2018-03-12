package at.shockbytes.corey.schedule

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.core.receiver.NotificationReceiver
import at.shockbytes.corey.storage.StorageManager
import at.shockbytes.corey.storage.live.LiveScheduleUpdateListener
import at.shockbytes.corey.util.CoreyAppUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Martin Macheiner
 * Date: 22.02.2017.
 */

class DefaultScheduleManager(private val storageManager: StorageManager,
                             private val context: Context,
                             private val preferences: SharedPreferences) : ScheduleManager {

    override val schedule: Observable<List<ScheduleItem>>
        get() = storageManager.schedule
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override val itemsForScheduling: Observable<List<String>>
        get() = storageManager.itemsForScheduling
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    override val isWorkoutNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_workout_day_notification_key), false)

    override val isWeighNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(context.getString(R.string.prefs_weigh_notification_key), false)

    override val dayOfWeighNotificationDelivery: Int
        get() = preferences.getInt(context.getString(R.string.prefs_weigh_notification_day_key), 0)

    override fun poke() {

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
        return storageManager.insertScheduleItem(item)
    }

    override fun updateScheduleItem(item: ScheduleItem) {
        storageManager.updateScheduleItem(item)
    }

    override fun deleteScheduleItem(item: ScheduleItem) {
        storageManager.deleteScheduleItem(item)
    }

    override fun postWeighNotification() {
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x90, CoreyAppUtils.getWeighNotification(context))
    }

    override fun tryPostWorkoutNotification() {
        schedule.subscribe({ scheduleItems ->
            scheduleItems
                    .filter { it.day == CoreyUtils.getDayOfWeek() && !it.isEmpty }
                    .firstOrNull { item ->
                        postWorkoutNotification(item)
                        true
                    }
        }, { throwable ->
            Log.wtf("Corey", "Cannot retrieve workouts: " + throwable.localizedMessage)
        })
    }

    override fun registerLiveForScheduleUpdates(listener: LiveScheduleUpdateListener) {
        storageManager.registerLiveScheduleUpdates(listener)
    }

    override fun unregisterLiveForScheduleUpdates() {
        storageManager.unregisterLiveScheduleUpdates()
    }

    private fun readNotificationTimeFromPreferences(): List<Int> {

        val str = preferences.getString(context.getString(R.string.prefs_workout_day_notification_daytime_key),
                context.getString(R.string.prefs_workout_day_notification_daytime_defValue))
        return str.split(":")
                .mapTo(mutableListOf()) { it.toInt() }
    }

    private fun postWorkoutNotification(item: ScheduleItem) {
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x91, CoreyAppUtils.getWorkoutNotification(context, item.name))
    }

}
