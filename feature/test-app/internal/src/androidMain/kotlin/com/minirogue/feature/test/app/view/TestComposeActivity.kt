package com.minirogue.feature.test.app.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.holocanon.library.navigation.AppBarConfig
import com.holocanon.library.navigation.NavContributor
import compose.theme.HolocanonTheme
import settings.model.DarkModeSetting
import settings.model.Theme

class TestComposeActivity : AppCompatActivity() {
    private val navContributors: Set<NavContributor> = emptySet() // TODO fix DI here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val appBarConfig = remember { mutableStateOf(AppBarConfig.DEFAULT) }
            HolocanonTheme(DarkModeSetting.SYSTEM, Theme.AndroidDynamic) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TestAppBar(appBarConfig.value) },
                ) { padding ->
                    NavHost(
                        modifier = Modifier.padding(padding),
                        navController = navController,
                        startDestination = navDestination!!, // TODO double bang
                    ) {
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TestAppBar(appBarConfig: AppBarConfig) = CenterAlignedTopAppBar(
        title = { Text(appBarConfig.title) },
        actions = {
            appBarConfig.actions.forEach { it() }
        },
    )

    companion object {
        private const val SCREEN_NAME_EXTRA = "screen name"
        private var navDestination: Any? = null
        fun newIntent(
            context: Context,
            screenName: String,
            newNavDestination: Any,
        ): Intent {
            navDestination = newNavDestination
            return Intent(context, TestComposeActivity::class.java).apply {
                putExtra(SCREEN_NAME_EXTRA, screenName)
            }
        }
    }
}
