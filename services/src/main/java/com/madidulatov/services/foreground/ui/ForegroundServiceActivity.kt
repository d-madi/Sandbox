package com.madidulatov.services.foreground.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.madidulatov.services.R
import com.madidulatov.services.foreground.service.TimerService
import com.madidulatov.utils.extentions.onClick
import kotlin.properties.Delegates

/**
 * TODO add docs
 */
class ForegroundServiceActivity : AppCompatActivity() {

    private var timerTextView: TextView by Delegates.notNull()
    private var button: Button by Delegates.notNull()
    private var timerService: TimerService by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreground_service)
        timerTextView = findViewById(R.id.activity_foreground_service_timer_txt)
        button = findViewById(R.id.activity_foreground_service_start_timer_btn)
        button.onClick {
            startTimerService()
        }
    }

    private fun startTimerService() {
        ContextCompat.startForegroundService(this, Intent(this, TimerService::class.java))
    }
}