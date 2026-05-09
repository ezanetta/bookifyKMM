package com.ezanetta.bookifykmm

import android.app.Application
import com.ezanetta.bookifykmm.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookifyApplication)
            modules(appModules)
        }
    }
}
