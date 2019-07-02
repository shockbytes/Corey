package at.shockbytes.corey.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import at.shockbytes.corey.ui.viewmodel.WearRunningViewModel
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
class ViewModelFactory @Inject constructor(
    private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T = viewModels[modelClass]?.get() as T
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class WearViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(WearRunningViewModel::class)
    internal abstract fun wearRunningViewModel(viewModel: WearRunningViewModel): ViewModel
}