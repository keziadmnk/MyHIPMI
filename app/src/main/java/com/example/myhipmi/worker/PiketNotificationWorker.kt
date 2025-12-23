package com.example.myhipmi.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myhipmi.data.local.UserSessionManager
import com.example.myhipmi.data.remote.retrofit.ApiConfig
import com.example.myhipmi.data.remote.response.CreatePiketNotificationRequest
import com.example.myhipmi.utils.PiketNotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class PiketNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val sessionManager = UserSessionManager(applicationContext)
            val idPengurus = sessionManager.getIdPengurus()
            
            if (idPengurus == null) {
                Log.d(TAG, "User not logged in, skipping notification")
                return@withContext Result.success()
            }

            val isPiketDay = checkPiketDay(idPengurus)
            
            if (isPiketDay) {
                Log.d(TAG, "Today is piket day for user $idPengurus, showing notification")
                PiketNotificationHelper.showPiketNotification(applicationContext)
                try {
                    val apiService = ApiConfig.getApiService()
                    val request = CreatePiketNotificationRequest(idPengurus = idPengurus)
                    val response = apiService.createPiketNotification(request)
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        Log.d(TAG, "Piket notification saved to database")
                    } else {
                        Log.w(TAG, "⚠Failed to save piket notification to database: ${response.message()}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving piket notification to database: ${e.message}", e)
                }
                
                Result.success()
            } else {
                Log.d(TAG, "Today is not piket day for user $idPengurus")
                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in PiketNotificationWorker: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun checkPiketDay(idPengurus: Int): Boolean {
        return try {
            val apiService = ApiConfig.getApiService()
            val response = apiService.getPengurusById(idPengurus)
            
            if (response.isSuccessful && response.body()?.success == true) {
                val pengurus = response.body()?.data
                val hariPiket = pengurus?.jadwalPiket?.hariPiket
                
                if (hariPiket != null) {
                    val hariSekarang = getCurrentDayName()
                    val isMatch = hariPiket.equals(hariSekarang, ignoreCase = true)
                    Log.d(TAG, "User piket day: $hariPiket, Today: $hariSekarang, Match: $isMatch")
                    return isMatch
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking piket day: ${e.message}", e)
            false
        }
    }

    private fun getCurrentDayName(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        
        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> ""
        }
    }

    companion object {
        private const val TAG = "PiketNotificationWorker"
    }
}

