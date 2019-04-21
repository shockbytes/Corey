package at.shockbytes.corey.data.reminder

import android.app.NotificationManager
import android.content.Context
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.storage.KeyValueStorage
import at.shockbytes.corey.util.CoreyAppUtils
import at.shockbytes.corey.util.isItemOfCurrentDay
import io.reactivex.Completable

class DefaultReminderManager(
    private val localStorage: KeyValueStorage,
    private val scheduleRepository: ScheduleRepository,
    private val schedulers: SchedulerFacade
) : ReminderManager {

    override var isWorkoutReminderEnabled: Boolean
        get() = localStorage.getBoolean(KEY_WORKOUT_REMINDER_ENABLED)
        set(value) = localStorage.putBoolean(value, KEY_WORKOUT_REMINDER_ENABLED)

    override var isWeighReminderEnabled: Boolean
        get() = localStorage.getBoolean(KEY_WEIGH_REMINDER_ENABLED)
        set(value) = localStorage.putBoolean(value, KEY_WEIGH_REMINDER_ENABLED)

    override var dayOfWeighReminder: Int
        get() = localStorage.getInt(KEY_REMINDER_WEIGH_DAY, REMINDER_DAY_DEFAULT_VALUE)
        set(value) = localStorage.putInt(value, KEY_REMINDER_WEIGH_DAY)

    override var hourOfWorkoutReminder: Int
        get() = localStorage.getInt(KEY_REMINDER_WORKOUT_HOUR, REMINDER_HOUR_DEFAULT_VALUE)
        set(value) = localStorage.putInt(value, KEY_REMINDER_WORKOUT_HOUR)

    override var hourOfWeighReminder: Int
        get() = localStorage.getInt(KEY_REMINDER_WEIGH_HOUR, REMINDER_HOUR_DEFAULT_VALUE)
        set(value) = localStorage.putInt(value, KEY_REMINDER_WEIGH_HOUR)

    override fun postWorkoutNotification(context: Context): Completable {
        return scheduleRepository.schedule
            .flatMapIterable { it }
            .filter { item ->
                // Filter all days which are not happening on the current day
                !item.isItemOfCurrentDay(CoreyUtils.getDayOfWeek())
            }
            .doOnNext { item ->
                postWorkoutNotificationForToday(context, item)
            }
            .flatMapCompletable {
                Completable.complete()
            }
            .subscribeOn(schedulers.io)
    }

    override fun postWeighNotification(context: Context): Completable {
        return Completable.fromCallable {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.notify(0x90, CoreyAppUtils.getWeighNotification(context))
        }
    }

    override fun shouldScheduleWeighReminder(): Boolean {
        return isWeighReminderEnabled && CoreyUtils.getDayOfWeek() == dayOfWeighReminder
    }

    private fun postWorkoutNotificationForToday(context: Context, item: ScheduleItem) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(0x91, CoreyAppUtils.getWorkoutNotification(context, item.name))
    }

    companion object {

        private const val KEY_WORKOUT_REMINDER_ENABLED = "key_workout_reminder"
        private const val KEY_WEIGH_REMINDER_ENABLED = "key_weigh_reminder"
        private const val KEY_REMINDER_WEIGH_DAY = "key_reminder_day"
        private const val KEY_REMINDER_WORKOUT_HOUR = "key_reminder_workout_hour"
        private const val KEY_REMINDER_WEIGH_HOUR = "key_reminder_weigh_hour"

        private const val REMINDER_DAY_DEFAULT_VALUE = 0
        private const val REMINDER_HOUR_DEFAULT_VALUE = 6
    }
}
