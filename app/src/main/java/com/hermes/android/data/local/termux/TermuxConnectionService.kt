package com.hermes.android.data.local.termux

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.hermes.android.R
import com.hermes.android.presentation.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

class TermuxConnectionService : Service() {

    companion object {
        const val CHANNEL_ID = "hermes_termux_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.hermes.android.START_HERMES"
        const val ACTION_STOP = "com.hermes.android.STOP_HERMES"
        const val ACTION_RESTART = "com.hermes.android.RESTART_HERMES"
        const val EXTRA_COMMAND = "command"
        const val EXTRA_OUTPUT_CALLBACK = "output_callback"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val currentProcess = AtomicReference<Process?>(null)
    private val outputChannel = Channel<String>(Channel.UNLIMITED)
    private val termuxPath = "/data/data/com.termux/files/usr/bin/bash"
    private var notificationManager: NotificationManager? = null
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        when (action) {
            ACTION_START -> {
                val command = intent.getStringExtra(EXTRA_COMMAND) ?: "hermes gateway --api-server-enabled"
                startHermes(command)
                showForegroundNotification("Starting Hermes…", "Initializing Termux process")
            }
            ACTION_STOP -> {
                stopHermes()
                stopSelf()
            }
            ACTION_RESTART -> {
                val command = intent.getStringExtra(EXTRA_COMMAND) ?: "hermes gateway --api-server-enabled"
                stopHermes()
                startHermes(command)
                showForegroundNotification("Restarting Hermes…", "Reinitializing Termux process")
            }
        }
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.notification_channel_default),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.notification_channel_default_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showForegroundNotification(title: String, text: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, TermuxConnectionService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        isRunning = true
    }

    private fun updateNotification(title: String, text: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_hermes_notification)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

    private fun startHermes(command: String) {
        isRunning = true
        scope.launch {
            try {
                val fullCommand = "$termuxPath -c \"$command\""
                val process = Runtime.getRuntime().exec(fullCommand)
                currentProcess.set(process)

                // Read stdout
                scope.launch {
                    java.io.BufferedReader(java.io.InputStreamReader(process.inputStream)).use { reader ->
                        reader.forEachLine { line ->
                            outputChannel.trySend(line)
                        }
                    }
                }

                // Read stderr
                scope.launch {
                    java.io.BufferedReader(java.io.InputStreamReader(process.errorStream)).use { reader ->
                        reader.forEachLine { line ->
                            outputChannel.trySend("[ERR] $line")
                        }
                    }
                }

                // Wait for process
                val exitCode = process.waitFor()
                currentProcess.set(null)
                isRunning = false

                if (exitCode != 0) {
                    updateNotification("Hermes Stopped", "Exit code: $exitCode")
                } else {
                    updateNotification("Hermes Stopped", "Process exited cleanly")
                }
            } catch (e: Exception) {
                currentProcess.set(null)
                isRunning = false
                updateNotification("Hermes Error", e.message ?: "Unknown error")
            }
        }
    }

    private fun stopHermes() {
        currentProcess.getAndSet(null)?.destroy()
        isRunning = false
    }

    fun getOutputChannel(): Channel<String> = outputChannel

    fun isHermesRunning(): Boolean = isRunning

    override fun onDestroy() {
        scope.cancel()
        stopHermes()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TermuxConnectionService = this@TermuxConnectionService
    }
}