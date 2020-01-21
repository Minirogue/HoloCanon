package com.minirogue.starwarscanontracker.dagger

import com.minirogue.starwarscanontracker.model.FilterUpdater
import com.minirogue.starwarscanontracker.model.TransferDatabase
import com.minirogue.starwarscanontracker.view.activity.MainActivity
import com.minirogue.starwarscanontracker.view.fragment.*
import com.minirogue.starwarscanontracker.viewmodel.FilterSelectionViewModel
import com.minirogue.starwarscanontracker.viewmodel.MediaListViewModel
import com.minirogue.starwarscanontracker.viewmodel.SeriesViewModel
import com.minirogue.starwarscanontracker.viewmodel.ViewMediaItemViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, RoomModule::class, ViewModelModule::class])
interface AppComponent {

    //Activity
    fun inject(mainActivity: MainActivity)

    //Fragments
    fun inject(aboutFragment: AboutFragment)
    fun inject(filterSelectionFragment: FilterSelectionFragment)
    fun inject(mediaListFragment: MediaListFragment)
    fun inject(seriesFragment: SeriesFragment)
    fun inject(viewMediaItemFragment: ViewMediaItemFragment)

    //ViewModels
    fun inject(filterSelectionViewModel: FilterSelectionViewModel)
    fun inject(mediaListViewModel: MediaListViewModel)
    fun inject(seriesViewModel: SeriesViewModel)
    fun inject(viewMediaItemViewModel: ViewMediaItemViewModel)

    //Database Management
    fun injectFilterUpdater(): FilterUpdater
    fun injectTransferDatabase(): TransferDatabase


}