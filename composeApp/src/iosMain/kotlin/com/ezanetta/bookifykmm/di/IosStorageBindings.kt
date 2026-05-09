package com.ezanetta.bookifykmm.di

import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.russhwolf.settings.NSUserDefaultsSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSUserDefaults

@ContributesTo(AppScope::class)
@BindingContainer
object IosStorageBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideLocalStorage(): LocalStorage =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
