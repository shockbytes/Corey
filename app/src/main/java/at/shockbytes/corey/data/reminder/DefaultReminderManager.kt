package at.shockbytes.corey.data.reminder

import android.app.NotificationManager
import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import at.shockbytes.core.scheduler.SchedulerFacade
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.data.reminder.worker.WeighNotificationWorker
import at.shockbytes.corey.data.reminder.worker.WorkoutNotificationWorker
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.storage.KeyValueStorage
import at.shockbytes.corey.util.CoreyAppUtils
import at.shockbytes.corey.util.isItemOfCurrentDay
import io.reactivex.Completable
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

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

    override fun poke(context: Context) {

        val hourToRemind = 6
        val initialDelayOffset = ReminderHelper.getInitialDelayOffset(DateTime.now(), hourToRemind)

        val workoutTag = "periodic_workout_notification_worker"
        val workoutRequest = buildNotificationRequest(initialDelayOffset, workoutTag, WorkoutNotificationWorker::class.java)

        val weighTag = "periodic_weigh_notification_worker"
        val weighRequest = buildNotificationRequest(initialDelayOffset, weighTag, WeighNotificationWorker::class.java)

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workoutTag, ExistingPeriodicWorkPolicy.REPLACE, workoutRequest)

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(weighTag, ExistingPeriodicWorkPolicy.REPLACE, weighRequest)
    }

    private fun buildNotificationRequest(
        initialDelayOffset: Minutes,
        tag: String,
        workerClass: Class<out ListenableWorker>
    ): PeriodicWorkRequest {
        return PeriodicWorkRequest
            .Builder(
                workerClass,
                24,
                TimeUnit.HOURS,
                PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .setInitialDelay(initialDelayOffset.minutes, TimeUnit.MINUTES)
            .addTag(tag)
            .build()
    }

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
