package com.minirogue.starwarscanontracker.view.activity

import androidx.lifecycle.ViewModel
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    private val getGlobalToasts: GetGlobalToasts
): ViewModel() {
    val globalToasts: Flow<String> = getGlobalToasts()
}