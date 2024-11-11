package com.minirogue.feature.test.app.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.minirogue.feature.test.app.model.TestScreen
import compose.theme.HolocanonTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class TestEntryActivity : AppCompatActivity() {

    @Inject
    lateinit var testScreens: List<@JvmSuppressWildcards TestScreen>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        testScreens.forEach { println("test-log ${it.screenName}") }
        setContent {
            HolocanonTheme {
                Scaffold() { padding ->
                    Column(modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()) {
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