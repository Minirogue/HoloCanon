package com.minirogue.starwarscanontracker.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.minirogue.starwarscanontracker.viewmodel.FilterSelectionViewModel
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import com.minirogue.starwarscanontracker.viewmodel.ViewMediaItemViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelFactory @Inject constructor(private val viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModels[modelClass]?.get() as T
    }
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
    @ViewModelKey(FilterSelectionViewModel::class)
    internal abstract fun bindFilterSelectionViewModel(viewModel: FilterSelectionViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MediaListViewModel::class)
    internal abstract fun bindMediaListViewModel(viewModel: MediaListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SeriesViewModel::class)
    internal abstract fun bindSeriesViewModel(viewModel: SeriesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ViewMediaItemViewModel::class)
    internal abstract fun bindViewMediaItemViewModel(viewModel: ViewMediaItemViewModel): ViewModel
}
