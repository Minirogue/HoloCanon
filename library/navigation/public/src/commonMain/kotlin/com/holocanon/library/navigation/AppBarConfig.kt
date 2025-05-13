package com.holocanon.library.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class AppBarConfig(
    val title: String = "",
    val actions: List<@Composable () -> Unit> = emptyList(),
)

data class AppBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)
