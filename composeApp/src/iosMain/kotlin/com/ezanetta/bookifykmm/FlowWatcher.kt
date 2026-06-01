package com.ezanetta.bookifykmm

import com.ezanetta.bookifykmm.domain.model.AppTheme
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyUiState
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.DetailViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FlowSubscription(private val scope: CoroutineScope) {
    fun cancel() = scope.cancel()
}

private fun <T> watch(flow: Flow<T>, callback: (T) -> Unit): FlowSubscription {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    flow.onEach { callback(it) }.launchIn(scope)
    return FlowSubscription(scope)
}

fun watchBookifyState(vm: BookifyViewModel, callback: (BookifyUiState) -> Unit): FlowSubscription =
    watch(vm.state, callback)

fun watchDetailIsWishlisted(vm: DetailViewModel, callback: (Boolean) -> Unit): FlowSubscription =
    watch(vm.isWishlisted, callback)

fun watchSettingsTheme(vm: SettingsViewModel, callback: (AppTheme) -> Unit): FlowSubscription =
    watch(vm.selectedTheme, callback)
