package com.madidulatov.services.foreground.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.madidulatov.services.helper.NotificationHelper

private val TIMER_SERVICE_NOTIFICATION_ID = "timer_notification_service".hashCode()

class TimerService : Service() {

    private val notificationHelper = NotificationHelper(this)

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

    private fun startTimer() {
        startForeground(TIMER_SERVICE_NOTIFICATION_ID, notificationHelper.getNotification())
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }
}