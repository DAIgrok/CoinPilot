package com.coinpilot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class CoinPilotWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val channelId = "coinpilot_daily"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Rotina diária", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("CoinPilot: hora da verificação")
            .setContentText("Confira as tarefas de moedas da Shopee e do AliExpress.")
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
        return Result.success()
    }

    companion object {
        fun scheduleDaily(context: Context) {
            val request = PeriodicWorkRequestBuilder<CoinPilotWorker>(24, TimeUnit.HOURS)
                .addTag("coinpilot_daily")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "coinpilot_daily_work",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}
