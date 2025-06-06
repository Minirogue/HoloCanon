package com.minirogue.feature.test.app.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.minirogue.feature.test.app.model.TestScreen
import compose.theme.HolocanonTheme
import dev.zacsweers.metro.Inject
import settings.model.DarkModeSetting
import settings.model.Theme

internal class TestEntryActivity : AppCompatActivity() {

    @Inject
    lateinit var testScreens: List<@JvmSuppressWildcards TestScreen>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HolocanonTheme(DarkModeSetting.SYSTEM, Theme.Force) {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                    ) {
                        testScreens.forEach { testScreen ->
                            Button(onClick = { testScreen.launchScreen(this@TestEntryActivity) }) {
                                Text(testScreen.screenName)
                            }
                        }
                    }
                }
            }
        }
    }
}
