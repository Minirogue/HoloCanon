package com.minirogue.starwarscanontracker.view.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.holocanon.app.shared.App
import com.holocanon.app.shared.MainActivityViewModel
import com.holocanon.library.navigation.NavContributor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navContributors: Set<@JvmSuppressWildcards NavContributor>

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App(navContributors)
        }

        mainActivityViewModel.globalToasts
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
            .launchIn(lifecycleScope) // TODO move to snackbar?
    }
}
