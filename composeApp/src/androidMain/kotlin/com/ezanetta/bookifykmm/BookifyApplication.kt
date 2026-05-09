package com.ezanetta.bookifykmm

import android.app.Application
import com.ezanetta.bookifykmm.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory

class BookifyApplication : Application() {
    lateinit var appGraph: AndroidAppGraph
        private set

    override fun onCreate() {
        super.onCreate()
        appGraph = createGraphFactory<AndroidAppGraph.Factory>().create(this)
    }
}
