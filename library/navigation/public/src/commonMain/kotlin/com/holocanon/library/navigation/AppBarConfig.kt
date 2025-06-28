package com.holocanon.library.navigation

import androidx.compose.runtime.Composable

data class AppBarConfig(
    val title: String,
    val actions: List<@Composable () -> Unit>,
) {
    companion object {
        val DEFAULT = AppBarConfig("", emptyList())
    }
}
