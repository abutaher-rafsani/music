package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

/**
 * NotificationHelper for dispatching active real-time push notifications 
 * for chat messages, scout gig alerts, and escrow bounty releases.
 */
class NotificationHelper(private val context: Context) {
    companion object {
        private const val CHANNEL_ID = "kliq_realtime_notifications"
        private const val CHANNEL_NAME = "KLIQ Active Notifications"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time updates for chat messages, bounties, and escrow releases"
                enableVibration(true)
                enableLights(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPushNotification(title: String, message: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            val notificationId = (System.currentTimeMillis() % 100000).toInt()
            notificationManager.notify(notificationId, builder.build())
        } catch (e: Exception) {
            // Fallback gracefully if notifications are disabled or not permitted
        }
    }
}
