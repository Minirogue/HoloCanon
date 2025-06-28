package com.holocanon.app.shared.di

import androidx.compose.ui.window.ComposeUIViewController
import com.holocanon.app.shared.App
import dev.zacsweers.metro.createGraphFactory


private val iosDependencyGraph =
    createGraphFactory<IosDependencyGraph.Factory>().create(object : PlatformDependencies {})

fun MainViewController() = ComposeUIViewController { App(iosDependencyGraph) }