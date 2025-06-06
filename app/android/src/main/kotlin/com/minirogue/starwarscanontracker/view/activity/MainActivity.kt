package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.holocanon.app.shared.App
import com.minirogue.starwarscanontracker.application.CanonTrackerApplication

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(CanonTrackerApplication.dependencyGraph)
        }
    }
}
