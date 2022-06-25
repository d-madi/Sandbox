package com.madidulatov.services.foreground.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.madidulatov.services.databinding.ActivityForegroundServiceBinding
import com.madidulatov.services.extension.secondsToTimeString
import com.madidulatov.services.foreground.model.TimerState
import com.madidulatov.services.foreground.service.SERVICE_TIMER_STATE
import com.madidulatov.services.foreground.service.TIME
import com.madidulatov.services.foreground.service.TimerService
import com.madidulatov.utils.extentions.onClick

/**
 * TODO add docs
 */
const val TIMER_ACTION = "timer_action"

class TimerServiceActivity : AppCompatActivity() {

    private val binding: ActivityForegroundServiceBinding by lazy {
        ActivityForegroundServiceBinding.inflate(layoutInflater)
    }
    private val timerReceiver: TimerReceiver by lazy { TimerReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupUi()
        initListeners()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(timerReceiver, IntentFilter(TIMER_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(timerReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimerService()
    }

    private fun setupUi() {
        binding.stopTimerButton.isEnabled = false
        binding.pauseTimerButton.isEnabled = false
        updateUi(0)
    }

    private fun initListeners() {
        binding.startTimerButton.onClick(::startTimerService)
        binding.pauseTimerButton.onClick(::pauseTimerService)
        binding.stopTimerButton.onClick(::stopTimerService)
    }

    private fun startTimerService() {
        binding.stopTimerButton.isEnabled = true
        binding.pauseTimerButton.isEnabled = true
        startForegroundTimerService(getTimerServiceIntent(TimerState.START))
    }

    private fun pauseTimerService() {
        startForegroundTimerService(getTimerServiceIntent(TimerState.PAUSE))
    }

    private fun stopTimerService() {
        binding.stopTimerButton.isEnabled = false
        binding.pauseTimerButton.isEnabled = false
        updateUi(0)
        startForegroundTimerService(getTimerServiceIntent(TimerState.STOP))
    }

    private fun startForegroundTimerService(intent: Intent) {
        ContextCompat.startForegroundService(this, intent)
    }

    private fun getTimerServiceIntent(
        timerState: TimerState
    ): Intent = Intent(this, TimerService::class.java)
        .putExtra(SERVICE_TIMER_STATE, timerState as Parcelable)

    private fun updateUi(time: Long) {
        binding.timerTxt.text = time.secondsToTimeString()
    }

    inner class TimerReceiver : BroadcastReceiver() {

        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (intent.action == TIMER_ACTION) {
                updateUi(intent.getLongExtra(TIME, 0))
            }
        }
    }
}