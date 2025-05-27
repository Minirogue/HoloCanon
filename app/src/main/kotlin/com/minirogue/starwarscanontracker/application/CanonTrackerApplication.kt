package com.minirogue.starwarscanontracker.application

import android.app.Application
import com.holocanon.library.coroutine.ext.ApplicationScope
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import com.minirogue.starwarscanontracker.core.usecase.UpdateFilters
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CanonTrackerApplication : Application() {
    @Inject
    lateinit var applicationScope: ApplicationScope

    @Inject
    lateinit var maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase

    @Inject
    lateinit var updateFilters: UpdateFilters

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            // TODO Filters should be managed a little more dynamically
            // Filters need to at least be initialized the first time the app is run
            updateFilters()
            // Update media database if needed.
            maybeUpdateMediaDatabase()
        }
    }
}
