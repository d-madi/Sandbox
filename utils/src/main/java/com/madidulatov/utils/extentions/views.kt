package com.madidulatov.utils.extentions

import android.view.View

fun View.onClick(action: () -> Unit) = setOnClickListener { action() }