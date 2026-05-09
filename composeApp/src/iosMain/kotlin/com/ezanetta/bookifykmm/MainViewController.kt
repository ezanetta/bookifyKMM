package com.ezanetta.bookifykmm

import androidx.compose.ui.window.ComposeUIViewController
import com.ezanetta.bookifykmm.di.appModules
import org.koin.core.context.startKoin

private val koinInit by lazy {
    startKoin { modules(appModules) }
}

fun MainViewController() = run {
    koinInit
    ComposeUIViewController { App() }
}
