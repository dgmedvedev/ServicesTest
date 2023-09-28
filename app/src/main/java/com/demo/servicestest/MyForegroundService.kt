package com.demo.servicestest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private val notificationBuilder by lazy {
        createNotificationBuilder()
    }

    var onProgressChanged: ((Int) -> Unit)? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            for (i in 0..100 step 5) {
                delay(1000)
                val notification = notificationBuilder
                    .setProgress(100, i, false)
                    .build()
                notificationManager.notify(NOTIFICATION_ID, notification)
                onProgressChanged?.invoke(i)
                log("Timer $i")
            }
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy()")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyForegroundService: $message")
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT // значение в зависимости от приоритета
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun createNotificationBuilder() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Title_foreground")
        .setContentText("Text_foreground")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setProgress(100, 0, false)
        .setOnlyAlertOnce(true)

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_foreground_id"
        private const val CHANNEL_NAME = "channel_foreground_name"

        fun newIntent(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }
    }
}