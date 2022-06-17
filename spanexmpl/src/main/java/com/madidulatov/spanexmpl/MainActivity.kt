package com.madidulatov.spanexmpl

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private var textview: TextView by Delegates.notNull()
    private var maskedEdittext: MaskedDateEditText by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textview = findViewById(R.id.textview)
        val spannable = SpannableString(textview.text)

        spannable.setSpan(
            ForegroundColorSpan(Color.GREEN),
            8,
            11,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        textview.text = spannable
        val text = "<p>Привет, <b>Мир</b>!</p>"
        textview.text = Html.fromHtml(text)
        maskedEdittext = findViewById(R.id.date_edittext)
    }
}