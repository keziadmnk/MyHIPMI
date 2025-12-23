package com.example.myhipmi.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myhipmi.utils.KasNotificationHelper
import java.util.Calendar

class KasNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("KasNotificationWorker", "Worker started")
        
        try {
            val calendar = Calendar.getInstance()
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
                KasNotificationHelper.showKasNotification(applicationContext)

            return Result.success()
        } catch (e: Exception) {
            Log.e("KasNotificationWorker", "Error showing notification", e)
            return Result.failure()
        }
    }
}
