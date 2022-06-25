package com.madidulatov.services.foreground.model

import android.os.Parcelable
import com.madidulatov.services.foreground.service.TimerService
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class TimerState : Parcelable {
    INITIALIZED,
    START,
    PAUSE,
    STOP
}