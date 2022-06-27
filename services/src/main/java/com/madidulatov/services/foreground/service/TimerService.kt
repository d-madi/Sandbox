package com.madidulatov.services.foreground.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.madidulatov.services.extension.secondsToTimeString
import com.madidulatov.services.foreground.model.TimerState
import com.madidulatov.services.foreground.ui.TIMER_ACTION
import com.madidulatov.services.helper.NotificationHelper
import com.madidulatov.utils.extentions.MainHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

const val TIME = "notification_text"
const val SERVICE_TIMER_STATE = "timer_state"
private const val UPDATE_TEXT_FORMAT = "Timer: %s"
private val TIMER_DELAY_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1)
private val TIMER_SERVICE_NOTIFICATION_ID = "timer_notification_service".hashCode()

class TimerService : Service(), CoroutineScope {

    private val notificationHelper = NotificationHelper(this)

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job

    private val handler = MainHandler
    private val runnable = object : Runnable {

        override fun run() {
            currentTimeMillis++
            updateBroadcast()
            handler.postDelayed(this, TIMER_DELAY_TIME_MILLIS)
        }
    }

    private var currentTimeMillis: Long = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.extras?.let {
            when(it.getParcelable<TimerState>(SERVICE_TIMER_STATE)) {
                TimerState.START -> startTimerService()
                TimerState.PAUSE -> pauseTimerService()
                TimerState.STOP -> stopTimerService()
                else -> return START_NOT_STICKY
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        job.cancel()
    }

    private fun startTimerService() {
        startForeground(TIMER_SERVICE_NOTIFICATION_ID, notificationHelper.getNotification())
        updateBroadcast()
        startTimer()
    }

    private fun pauseTimerService() {
        handler.removeCallbacks(runnable)
        updateBroadcast()
    }

    private fun stopTimerService() {
        handler.removeCallbacks(runnable)
        currentTimeMillis = 0
        updateBroadcast()
        stopService()
    }

    private fun updateBroadcast() {
        sendBroadcast(
            Intent(TIMER_ACTION).putExtra(TIME, currentTimeMillis)
        )
        notificationHelper.updateNotification(TIMER_SERVICE_NOTIFICATION_ID, UPDATE_TEXT_FORMAT.format(currentTimeMillis.secondsToTimeString()))
    }

    private fun startTimer() {
        launch {
            handler.post(runnable)
        }
    }

    private fun stopService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true)
        } else {
            stopSelf()
        }
    }
}