package com.ezanetta.bookifykmm.di

import com.ezanetta.bookifykmm.data.local.LocalStorage
import com.russhwolf.settings.NSUserDefaultsSettings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    single<LocalStorage> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
    }
}
