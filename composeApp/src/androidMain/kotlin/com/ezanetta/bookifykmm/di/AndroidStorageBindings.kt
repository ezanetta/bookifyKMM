package com.ezanetta.bookifykmm.di

import android.content.Context
import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.russhwolf.settings.SharedPreferencesSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object AndroidStorageBindings {
    @Provides
    @SingleIn(AppScope::class)
    fun provideLocalStorage(context: Context): LocalStorage =
        SharedPreferencesSettings(context.getSharedPreferences("bookify_prefs", Context.MODE_PRIVATE))
}
