package at.shockbytes.corey.data.reminder.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single

class WeighNotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
): RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}