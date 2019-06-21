package at.shockbytes.corey.storage.running

import android.content.SharedPreferences
import at.shockbytes.corey.common.core.running.Run
import io.reactivex.Completable
import io.reactivex.Single

class SharedPreferencesRunningStorageRepository(
    private val sharedPreferences: SharedPreferences
) : RunningStorageRepository {

    override fun storeRun(run: Run): Completable {
        // TODO
        return Completable.complete()
    }

    override fun getRuns(): Single<List<Run>> {
        // TODO
        return Single.just(listOf())
    }
}