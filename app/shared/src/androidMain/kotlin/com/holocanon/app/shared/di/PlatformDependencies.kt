package com.holocanon.app.shared.di

import android.app.Application

actual interface PlatformDependencies {
    val application: Application
}
