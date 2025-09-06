package com.minirogue.feature.test.app.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.holocanon.library.navigation.AppBarConfig
import com.minirogue.feature.test.app.TestAppDi
import compose.theme.HolocanonTheme
import dev.zacsweers.metro.createGraph
import kotlinx.serialization.Serializable
import settings.model.DarkModeSetting
import settings.model.Theme

internal class TestEntryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navContributors = TestAppDi.instance.navContributors

        setContent {
            HolocanonTheme(DarkModeSetting.SYSTEM, Theme.Force) {
                val navController = rememberNavController()
                val appBarConfig = remember { mutableStateOf(AppBarConfig.DEFAULT) }
                Scaffold(
                    topBar = { TestAppBar(appBarConfig.value) },
                ) { padding ->
                    NavHost(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        startDestination = TestAppEntryNav, // TODO double bang
                    ) {
                        composable<TestAppEntryNav> {
                            TestAppEntryScreen(
                                navController = navController,
                            )
                        }
                        navContributors.forEach {
                            it.invoke(
                                this,
                                navController,
                            ) { appBarConfig.value = it }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TestAppBar(appBarConfig: AppBarConfig) = CenterAlignedTopAppBar(
    title = { Text(appBarConfig.title) },
    actions = {
        appBarConfig.actions.forEach { it() }
    },
)

@Composable
private fun TestAppEntryScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) = Column(
    modifier = modifier.fillMaxSize(),
) {
    navController.graph
        .filter { it.route?.contains(TestAppEntryNav.toString()) != true }
        .forEach { navDestination ->
            Button(onClick = { navController.navigate(navDestination.route ?: "no route") }) {
                Text(navDestination.route?.split(".")?.last() ?: "route not found")
            }
        }
}

@Serializable
private data object TestAppEntryNav
