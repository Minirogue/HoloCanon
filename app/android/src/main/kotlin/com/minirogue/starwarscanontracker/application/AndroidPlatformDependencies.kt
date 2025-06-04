package com.minirogue.starwarscanontracker.application

import android.app.Application
import com.holocanon.app.shared.di.PlatformDependencies

data class AndroidPlatformDependencies(override val application: Application) : PlatformDependencies
