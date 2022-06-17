package com.madidulatov.services.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.madidulatov.services.R
import com.madidulatov.services.ServiceActivity

private const val CHANNEL_ID = "ServicesChannel"
private const val CHANNEL_NAME = "ServicesChannelName"
private const val CHANNEL_DESCRIPTION = "ServicesChannelDescription"
private const val RC_CODE = 0

class NotificationHelper(private val context: Context) {

    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun getNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(createChannel())
        }

        return buildNotification()
    }

    fun updateNotification(
        notificationId: Int,
        contentText: String
    ) {
        notificationManager.notify(
            notificationId,
            buildNotification {
                setContentText(contentText)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(): NotificationChannel = NotificationChannel(
        CHANNEL_ID,
        CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = CHANNEL_DESCRIPTION
        setSound(null, null)
    }

    private fun buildNotification(
        block: (NotificationCompat.Builder.() -> Unit)? = null
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(context.getString(R.string.app_name))
            .setSound(null)
            .setContentIntent(createPendingIntent())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setAutoCancel(true)
        block?.invoke(builder)

        return builder.build()
    }

    private fun createPendingIntent(): PendingIntent = PendingIntent.getActivity(
        context,
        RC_CODE,
        Intent(context, ServiceActivity::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}