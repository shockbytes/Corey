package at.shockbytes.corey.data.reminder.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import at.shockbytes.corey.common.core.util.CoreyUtils
import at.shockbytes.corey.dagger.ChildWorkerFactory
import at.shockbytes.corey.data.reminder.ReminderManager
import io.reactivex.Single
import javax.inject.Inject

class WeighNotificationWorker(
    private val reminderManager: ReminderManager,
    private val appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        return if (shouldPostWeighNotification()) {
            reminderManager.postWeighNotification(appContext)
                .toSingle {
                    Result.success()
                }
                .onErrorReturn {
                    Result.failure()
                }
        } else {
            // Reminder is disabled, still indicate the success state
            Single.just(Result.success())
        }
    }

    private fun shouldPostWeighNotification(): Boolean {
        return (reminderManager.isWeighReminderEnabled && reminderManager.dayOfWeighReminder == CoreyUtils.getDayOfWeek())
    }

    class Factory @Inject constructor(
        private val reminderManager: ReminderManager
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return WeighNotificationWorker(reminderManager, appContext, params)
        }
    }
}