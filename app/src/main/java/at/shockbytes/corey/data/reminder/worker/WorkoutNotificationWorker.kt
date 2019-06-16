package at.shockbytes.corey.data.reminder.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import at.shockbytes.corey.dagger.ChildWorkerFactory
import at.shockbytes.corey.data.reminder.ReminderManager
import io.reactivex.Single
import javax.inject.Inject

class WorkoutNotificationWorker(
    private val reminderManager: ReminderManager,
    private val appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        return if (reminderManager.isWorkoutReminderEnabled) {
            reminderManager.postWorkoutNotification(appContext)
                .map {
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

    class Factory @Inject constructor(
        private val reminderManager: ReminderManager
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): ListenableWorker {
            return WorkoutNotificationWorker(reminderManager, appContext, params)
        }
    }
}