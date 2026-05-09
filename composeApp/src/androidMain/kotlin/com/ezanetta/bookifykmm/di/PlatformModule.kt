package com.ezanetta.bookifykmm.di

import android.content.Context
import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<LocalStorage> {
        SharedPreferencesSettings(get<Context>().getSharedPreferences("bookify_prefs", Context.MODE_PRIVATE))
    }
}
