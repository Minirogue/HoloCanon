package com.holocanon.app.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.holocanon.feature.global.notification.usecase.GetGlobalToasts
import com.holocanon.library.filters.usecase.UpdateFilters
import com.minirogue.holoclient.usecase.MaybeUpdateMediaDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import settings.model.DarkModeSetting
import settings.model.Theme
import settings.usecase.GetAllSettings
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getGlobalToasts: GetGlobalToasts,
    getSettings: GetAllSettings,
    private val maybeUpdateMediaDatabase: MaybeUpdateMediaDatabase,
    private val updateFilters: UpdateFilters,
) : ViewModel() {
    val globalToasts: Flow<String> = getGlobalToasts()
    val themeSettings: Flow<Pair<DarkModeSetting, Theme>> =
        getSettings().map { Pair(it.darkModeSetting, it.theme) }

    fun onAppStart() = viewModelScope.launch {
        // TODO Filters should be managed a little more dynamically
        // Filters need to at least be initialized the first time the app is run
        updateFilters()
        // Update media database if needed.
        maybeUpdateMediaDatabase()
    }
}
