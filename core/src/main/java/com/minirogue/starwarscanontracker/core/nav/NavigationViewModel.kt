package com.minirogue.starwarscanontracker.core.nav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

sealed interface NavigationDestination {
    data class MediaItemScreen(val itemId: Long) : NavigationDestination
    data class SeriesScreen(val seriesId: Int) : NavigationDestination
}

class NavigationViewModel : ViewModel() {
    private val _navigationDestination = MutableSharedFlow<NavigationDestination>(replay = 0, extraBufferCapacity = 5)
    val navigationDestination: SharedFlow<NavigationDestination> = _navigationDestination

    fun navigateTo(destination: NavigationDestination) = viewModelScope.launch {
        _navigationDestination.emit(destination)
    }
}

