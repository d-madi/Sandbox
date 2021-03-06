package com.madidulatov.viewmodelexmpl.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

object AppDi {

    fun start(application: Application) {
        startKoin {
            androidContext(application)
            modules(
                dataModule,
                domainModule
            )
            androidLogger(Level.DEBUG)
        }
    }
}