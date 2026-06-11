package com.hermes.android.presentation.ui.local

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hermes.android.R
import com.hermes.android.domain.model.TermuxStatus
import com.hermes.android.presentation.ui.MainActivity

class TermuxNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "hermes_termux_notifications"
        const val NOTIFICATION_ID_RUNNING = 2001
        const val NOTIFICATION_ID_STOPPED = 2002
        const val NOTIFICATION_ID_ERROR = 2003
    }

    private val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for Hermes Termux status"
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun notifyStatusChange(status: TermuxStatus, previousStatus: TermuxStatus?) {
        if (!status.isInstalled) {
            notifyTermuxNotInstalled()
            return
        }

        if (!status.hermesInstalled) {
            notifyHermesNotInstalled()
            return
        }

        val wasRunning = previousStatus?.isRunning == true
        val isRunning = status.isRunning

        if (isRunning && !wasRunning) {
            notifyHermesStarted()
        } else if (!isRunning && wasRunning) {
            notifyHermesStopped(status.error)
        } else if (isRunning && status.error != null) {
            notifyHermesError(status.error!!)
        }
    }

    private fun notifyTermuxNotInstalled() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Termux Not Installed")
            .setContentText("Install Termux from F-Droid to run Hermes locally")
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ERROR, notification)
    }

    private fun notifyHermesNotInstalled() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Hermes Not Installed in Termux")
            .setContentText("Tap to install Hermes in Termux")
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ERROR, notification)
    }

    private fun notifyHermesStarted() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Hermes Started")
            .setContentText("Local Hermes Gateway is now running")
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIFICATION_ID_RUNNING, notification)
    }

    private fun notifyHermesStopped(error: String?) {
        val title = if (error != null) "Hermes Stopped Unexpectedly" else "Hermes Stopped"
        val text = if (error != null) "Error: $error" else "Local Hermes Gateway has stopped"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(if (error != null) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(NOTIFICATION_ID_STOPPED, notification)
    }

    private fun notifyHermesError(error: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Hermes Error")
            .setContentText(error)
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ERROR)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(NOTIFICATION_ID_ERROR, notification)
    }

    fun clearAll() {
        notificationManager.cancel(NOTIFICATION_ID_RUNNING)
        notificationManager.cancel(NOTIFICATION_ID_STOPPED)
        notificationManager.cancel(NOTIFICATION_ID_ERROR)
    }
}