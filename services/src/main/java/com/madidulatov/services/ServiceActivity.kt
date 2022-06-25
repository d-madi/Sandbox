package com.madidulatov.services

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.madidulatov.services.foreground.ui.TimerServiceActivity
import com.madidulatov.utils.extentions.makeStartIntent
import com.madidulatov.utils.extentions.onClick

class ServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        findViewById<View>(R.id.activity_service_foreground_btn).onClick {
            startActivity(
                makeStartIntent(TimerServiceActivity::class.java) {
                    putExtra("Test", "Test")
                }
            )
        }
    }
}