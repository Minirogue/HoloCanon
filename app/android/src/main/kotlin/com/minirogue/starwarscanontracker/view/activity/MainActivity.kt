package com.minirogue.starwarscanontracker.view.activity

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.holocanon.app.shared.App
import com.holocanon.app.shared.di.PlatformDependencies

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val platformDependencies = object : PlatformDependencies {
            override val application: Application = getApplication()
        }
        setContent {
            App(platformDependencies)
        }
    }
}
