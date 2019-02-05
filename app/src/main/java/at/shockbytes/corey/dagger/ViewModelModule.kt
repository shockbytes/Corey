package at.shockbytes.corey.dagger

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import at.shockbytes.corey.ui.viewmodel.BodyViewModel
import at.shockbytes.corey.ui.viewmodel.GoalsViewModel
import at.shockbytes.corey.ui.viewmodel.MainViewModel
import at.shockbytes.corey.ui.viewmodel.SignupViewModel
import at.shockbytes.corey.ui.viewmodel.WorkoutOverviewViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Author:  Martin Macheiner
 * Date:    12.06.2018
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(SignupViewModel::class)
    internal abstract fun signupViewModel(viewModel: SignupViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    internal abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BodyViewModel::class)
    internal abstract fun bodyViewModel(viewModel: BodyViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GoalsViewModel::class)
    internal abstract fun goalsViewModel(viewModel: GoalsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WorkoutOverviewViewModel::class)
    internal abstract fun workoutOverviewViewModel(viewModel: WorkoutOverviewViewModel): ViewModel
}