package com.bdjobs.app.application

import android.app.Application
import com.bdjobs.app.BuildConfig
import com.bdjobs.app.ajkerDeal.di.appModule
import com.bdjobs.app.ajkerDeal.utilities.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        SessionManager.init(applicationContext)
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule))
        }
    }
}