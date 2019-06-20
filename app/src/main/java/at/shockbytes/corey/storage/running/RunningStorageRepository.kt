package at.shockbytes.corey.storage.running

import at.shockbytes.corey.common.core.running.Run
import io.reactivex.Completable
import io.reactivex.Single

interface RunningStorageRepository {

    fun storeRun(run: Run): Completable

    fun getRuns(): Single<List<Run>>
}