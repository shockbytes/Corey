package at.shockbytes.corey.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import at.shockbytes.corey.core.CoreyApp
import at.shockbytes.corey.data.reminder.ReminderManager
import timber.log.Timber
import javax.inject.Inject

class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var manager: ReminderManager

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as CoreyApp).appComponent.inject(this)

        if (manager.shouldScheduleWeighReminder()) {
            manager.postWeighNotification(context)
                .subscribe({
                    Timber.d("Weigh notification successfully posted!")
                }, { throwable ->
                    Timber.e(throwable)
                })
        }
        if (manager.isWorkoutReminderEnabled) {
            manager.postWorkoutNotification(context)
                .subscribe({
                    Timber.d("Workout notification successfully posted!")
                }, { throwable ->
                    Timber.e(throwable)
                })
        }
    }
}
