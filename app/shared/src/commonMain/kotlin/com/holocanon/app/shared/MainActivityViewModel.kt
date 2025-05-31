package com.holocanon.app.shared

import androidx.lifecycle.ViewModel
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import settings.model.DarkModeSetting
import settings.model.Theme
import settings.usecase.GetAllSettings
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getGlobalToasts: GetGlobalToasts,
    getSettings: GetAllSettings,
) : ViewModel() {
    val globalToasts: Flow<String> = getGlobalToasts()
    val themeSettings: Flow<Pair<DarkModeSetting, Theme>> =
        getSettings().map { Pair(it.darkModeSetting, it.theme) }
}
