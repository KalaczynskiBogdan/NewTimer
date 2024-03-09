package com.example.newtimer.workmanager

import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.newtimer.helpers.NotificationHelper
import kotlinx.coroutines.delay
import java.io.IOException

class CounterWorker(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    private val notificationHelper by lazy {
        NotificationHelper(appContext)
    }
    private var elapsedTimeInSeconds: Long = 0
    companion object{
        private val elapsedTimeLiveData = MutableLiveData<Long>()

        fun getTime(): LiveData<Long> {
            return elapsedTimeLiveData
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NotificationHelper.NOTIFICATION_ID,
            notificationHelper.getNotification(),
            FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
        )
    }

    override suspend fun doWork(): Result {

        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        setForeground(createForegroundInfo())
        notificationHelper.setWorkingIntent(intent)

        try {
            while (elapsedTimeInSeconds < 60) {
                elapsedTimeInSeconds++

                elapsedTimeLiveData.postValue(elapsedTimeInSeconds)

                notificationHelper.updateNotification(elapsedTimeInSeconds.toString())
                delay(1000)
            }

        } catch (e: IOException) {
            return Result.retry()
        }
        return Result.success()
    }
}