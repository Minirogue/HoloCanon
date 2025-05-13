package com.minirogue.starwarscanontracker.view.activity

import androidx.lifecycle.ViewModel
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.model.DarkModeSetting
import settings.usecase.GetAllSettings
import javax.inject.Inject

@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    getGlobalToasts: GetGlobalToasts,
    getSettings: GetAllSettings,
) : ViewModel() {
    val globalToasts: Flow<String> = getGlobalToasts()
    val darkModeSetting: Flow<DarkModeSetting> = getSettings().map { it.darkModeSetting }
}
