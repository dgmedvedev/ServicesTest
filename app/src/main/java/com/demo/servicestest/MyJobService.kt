package com.demo.servicestest

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import kotlinx.coroutines.*

class MyJobService : JobService() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate()")
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        log("onStartCommand()")
        coroutineScope.launch {
            for (i in 0 until 100) {
                delay(1000)
                log("Timer $i")
            }
        }
        return true // т.к. асинхронная работа
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        log("onStopJob")
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy()")
    }

    private fun log(message: String) {
        Log.d("SERVICE_TAG", "MyService: $message")
    }
}