package at.shockbytes.corey.data.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import at.shockbytes.corey.R
import at.shockbytes.corey.common.core.WeightUnit
import at.shockbytes.corey.common.core.util.UserSettings
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.data.body.BodyRepository
import at.shockbytes.corey.data.body.model.User
import at.shockbytes.corey.data.reminder.worker.WeighNotificationWorker
import at.shockbytes.corey.data.reminder.worker.WorkoutNotificationWorker
import at.shockbytes.corey.data.schedule.ScheduleItem
import at.shockbytes.corey.data.schedule.ScheduleRepository
import at.shockbytes.corey.storage.KeyValueStorage
import at.shockbytes.corey.util.asCompletable
import at.shockbytes.corey.util.isItemOfCurrentDay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit

class DefaultReminderManager(
    private val localStorage: KeyValueStorage,
    private val scheduleRepository: ScheduleRepository,
    private val bodyRepository: BodyRepository,
    private val userSettings: UserSettings
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
        createNotificationChannel(context)

        postWorkoutNotificationWorker(context)
        postWeighNotificationWorker(context)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(context) {
                val name = getString(R.string.corey_notification_channel_name)
                val description = getString(R.string.corey_notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT

                val channel = NotificationChannel(
                    getString(R.string.corey_notification_channel_id),
                    name,
                    importance
                ).apply {
                    this.description = description
                    enableLights(true)
                    setShowBadge(true)
                }

                getNotificationManager(this).createNotificationChannel(channel)
            }
        }
    }

    private fun getNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun postWeighNotificationWorker(context: Context) {
        val weighTag = "periodic_weigh_notification_worker"
        val initialWeighDelayOffset = ReminderHelper.getInitialDelayOffset(DateTime.now(), hourOfWeighReminder)
        val weighRequest = buildNotificationRequest(initialWeighDelayOffset, weighTag, WeighNotificationWorker::class.java)

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(weighTag, ExistingPeriodicWorkPolicy.REPLACE, weighRequest)
    }

    private fun postWorkoutNotificationWorker(context: Context) {
        val workoutTag = "periodic_workout_notification_worker"
        val initialWorkoutDelayOffset = ReminderHelper.getInitialDelayOffset(DateTime.now(), hourOfWorkoutReminder)
        val workoutRequest = buildNotificationRequest(initialWorkoutDelayOffset, workoutTag, WorkoutNotificationWorker::class.java)

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(workoutTag, ExistingPeriodicWorkPolicy.REPLACE, workoutRequest)
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

    override fun postWorkoutNotification(context: Context): Single<ScheduleItem> {
        return scheduleRepository.schedule
            .flatMapIterable { it }
            .filter { item ->
                // Filter all days which are not happening on the current day
                item.isItemOfCurrentDay(CoreyUtils.getDayOfWeek())
            }
            .firstOrError()
            .doOnSuccess { item ->
                postWorkoutNotificationForToday(context, item)
            }
    }

    override fun postWeighNotification(context: Context): Completable {
        return retrieveWeighData()
            .doOnNext { (user, weightUnit) ->

                val notification = ReminderNotificationBuilder.buildWeighNotification(
                    context,
                    CoreyUtils.getLocalizedDayOfWeek(context),
                    weight = String.format("%,.0f", user.currentWeight),
                    weightUnit = weightUnit.acronym
                )

                getNotificationManager(context).run {
                    notify(0x90, notification)
                }
            }
            .asCompletable()
    }

    private fun retrieveWeighData(): Observable<Pair<User, WeightUnit>> {
        return Observable
            .zip(
                bodyRepository.user,
                userSettings.weightUnit,
                { user, unit -> Pair(user, unit) }
            )
    }

    override fun shouldScheduleWeighReminder(): Boolean {
        return isWeighReminderEnabled && CoreyUtils.getDayOfWeek() == dayOfWeighReminder
    }

    private fun postWorkoutNotificationForToday(context: Context, item: ScheduleItem) {

        val notification = ReminderNotificationBuilder.buildWorkoutNotification(
            context,
            item.name,
            item.workoutIconType
        )

        getNotificationManager(context).run {
            notify(0x91, notification)
        }
    }

    companion object {

        private const val KEY_WORKOUT_REMINDER_ENABLED = "key_workout_reminder"
        private const val KEY_WEIGH_REMINDER_ENABLED = "key_weigh_reminder"
        private const val KEY_REMINDER_WEIGH_DAY = "key_reminder_day"
        private const val KEY_REMINDER_WORKOUT_HOUR = "key_reminder_workout_hour"
        private const val KEY_REMINDER_WEIGH_HOUR = "key_reminder_weigh_hour"

        private const val REMINDER_DAY_DEFAULT_VALUE = 3 // Thursday is the default weigh day
        private const val REMINDER_HOUR_DEFAULT_VALUE = 6
    }
}
