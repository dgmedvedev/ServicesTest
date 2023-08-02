package com.demo.servicestest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.demo.servicestest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var page = 0

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.simpleService.setOnClickListener {
            stopService(MyForegroundService.newIntent(this))
            startService(MyService.newIntent(this, 25))
        }

        binding.foregroundService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyForegroundService.newIntent(this)
            )
        }

        binding.intentService.setOnClickListener {
            ContextCompat.startForegroundService(
                this,
                MyIntentService.newIntent(this)
            )
        }

        binding.jobScheduler.setOnClickListener {
            // указываем какой именно сервис нужен
            val componentName = ComponentName(this, MyJobService::class.java)
            // устанавливаем все ограничение, которые нужны
            val jobInfo = JobInfo.Builder(MyJobService.JOB_ID, componentName)
                .setExtras(MyJobService.newBundle(page++)) // передаем параметр
                .setRequiresCharging(true) // устройство на зарядке
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED) // только с WiFi
                .build()
            // запускаем сервис на выполнение
            val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(jobInfo)
        }
    }
}