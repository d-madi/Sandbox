package com.madidulatov.utils.extentions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

inline fun buildIntent(intentBlock: Intent.() -> Unit): Intent {
    val intent = Intent()
    intentBlock.invoke(intent)

    return intent
}

fun <T : AppCompatActivity> AppCompatActivity.startActivity(
    activityClass: Class<T>,
    extrasBlock: (Intent.() -> Unit)? = null
): Intent {
    val intent = Intent(this, activityClass)
    extrasBlock?.invoke(intent)

    return intent
}