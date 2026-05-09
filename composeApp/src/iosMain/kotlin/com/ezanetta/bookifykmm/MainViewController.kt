package com.ezanetta.bookifykmm

import androidx.compose.ui.window.ComposeUIViewController
import com.ezanetta.bookifykmm.di.IosAppGraph
import dev.zacsweers.metro.createGraph

private val appGraph: IosAppGraph by lazy { createGraph<IosAppGraph>() }

fun MainViewController() = ComposeUIViewController {
    App(graph = appGraph)
}
