package at.shockbytes.corey.data.reminder.worker

import android.app.NotificationManager
import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import at.shockbytes.corey.data.reminder.ReminderNotificationBuilder
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

class TestWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        return Single
            .fromCallable {
                Result.success()
            }
            .doOnSuccess {
                val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(0x90, ReminderNotificationBuilder.buildWeighNotification(appContext, "Sunday", "82.0", "kg"))
            }
            .subscribeOn(AndroidSchedulers.mainThread())
    }
}