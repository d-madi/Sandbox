package com.madidulatov.services.foreground.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.madidulatov.services.R
import com.madidulatov.services.foreground.service.TimerService
import kotlin.properties.Delegates

/**
 * TODO add docs
 */
class ForegroundServiceActivity : AppCompatActivity() {

    private var timerTextView: TextView by Delegates.notNull()
    private var timerService: TimerService by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foreground_service)
        timerTextView = findViewById(R.id.activity_foreground_service_timer_txt)

    }
}