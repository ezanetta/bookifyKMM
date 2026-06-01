package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.di.IosAppGraph
import dev.zacsweers.metro.createGraph

object AppContainer {
    val graph: IosAppGraph by lazy { createGraph<IosAppGraph>() }
}
