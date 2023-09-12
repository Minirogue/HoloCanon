package com.minirogue.starwarscanontracker.application

import ApplicationScope
import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope

@HiltAndroidApp
class CanonTrackerApplication : Application(), ImageLoaderFactory {
    val appScope : ApplicationScope = ApplicationScope.from(MainScope())

    override fun newImageLoader(): ImageLoader = ImageLoader.Builder(applicationContext)
        .build()
}
