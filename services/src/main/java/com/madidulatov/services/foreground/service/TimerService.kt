package com.madidulatov.services.foreground.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
class TimerService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        println("TimerService.onCreate()")
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        println("TimerService.onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("TimerService.onDestroy()")
    }
}