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
                             private val cxt: Context,
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
        get() = preferences.getBoolean(cxt.getString(R.string.prefs_workout_day_notif_key), false)

    override val isWeighNotificationDeliveryEnabled: Boolean
        get() = preferences.getBoolean(cxt.getString(R.string.prefs_weigh_notif_key), false)

    override val dayOfWeighNotificationDelivery: Int
        get() = preferences.getInt(cxt.getString(R.string.prefs_weigh_notif_day_key), 0)

    override fun poke() {

        val alarmManager = cxt.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(cxt, NotificationReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(cxt, 0x9238, intent, 0)

        // TODO v2.1 Do not hardcode hour and minute
        val hour = 8
        val minute = 30

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
        val nm = cxt.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x90, CoreyAppUtils.getWeighNotification(cxt))
    }

    override fun tryPostWorkoutNotification() {
        schedule.subscribe({ scheduleItems ->
            for (item in scheduleItems) {
                if (item.day == CoreyUtils.getDayOfWeek() && !item.isEmpty) {
                    val nm = cxt.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(0x91, CoreyAppUtils.getWorkoutNotification(cxt, item.name))
                    return@subscribe
                }
            }
        }, { throwable -> Log.wtf("Corey", "Cannot retrieve workouts: " + throwable.localizedMessage) })
    }

    override fun registerLiveForScheduleUpdates(listener: LiveScheduleUpdateListener) {
        storageManager.registerLiveScheduleUpdates(listener)
    }

    override fun unregisterLiveForScheduleUpdates() {
        storageManager.unregisterLiveScheduleUpdates()
    }

}
