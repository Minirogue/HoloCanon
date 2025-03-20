package com.minirogue.starwarscanontracker.view

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

context(Fragment)
fun <T> Flow<T>.collectWithLifecycle(
    minimumLifecycle: Lifecycle.State = Lifecycle.State.STARTED,
    action: (T) -> Unit,
) {
    flowWithLifecycle(viewLifecycleOwner.lifecycle, minimumLifecycle)
        .onEach(action)
        .launchIn(viewLifecycleOwner.lifecycleScope)
}
