package at.shockbytes.corey.dagger

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import at.shockbytes.corey.data.reminder.worker.WeighNotificationWorker
import at.shockbytes.corey.data.reminder.worker.WorkoutNotificationWorker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
@Singleton
class WorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return workerFactories[Class.forName(workerClassName)]?.get()?.create(appContext, workerParameters)
    }
}

@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
abstract class WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(WeighNotificationWorker::class)
    internal abstract fun bindWeighNotificationWorker(worker: WeighNotificationWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(WorkoutNotificationWorker::class)
    internal abstract fun bindWorkoutNotificationWorker(worker: WorkoutNotificationWorker.Factory): ChildWorkerFactory
}

interface ChildWorkerFactory {
    fun create(appContext: Context, params: WorkerParameters): ListenableWorker
}