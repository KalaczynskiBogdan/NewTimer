package com.example.newtimer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.newtimer.helpers.NotificationHelper
import com.example.newtimer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ForegroundService : Service(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + Job()

    private var countDownTimer: CountDownTimer? = null

    private var elapsedTimeInSeconds: Long = 0

    private val notificationHelper by lazy {
        NotificationHelper(this)
    }

    companion object {

        private val elapsedTimeLiveData = MutableLiveData<Long>()

        fun startService(context: Context) {
            val startIntent = Intent(context, ForegroundService::class.java)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }

        fun getTime(): LiveData<Long> {
            return elapsedTimeLiveData
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        countDownTimer = object : CountDownTimer(59000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                CoroutineScope(coroutineContext).launch {
                    elapsedTimeInSeconds++

                    elapsedTimeLiveData.postValue(elapsedTimeInSeconds)

                    notificationHelper.updateNotification(elapsedTimeInSeconds.toString())
                }
            }

            override fun onFinish() {
                elapsedTimeInSeconds = 0
                elapsedTimeLiveData.postValue(elapsedTimeInSeconds)

                notificationHelper.updateNotification(elapsedTimeInSeconds.toString())
                countDownTimer?.cancel()
                stopSelf()
            }
        }
        countDownTimer?.start()
        startForeground(NOTIFICATION_ID, notificationHelper.getNotification())

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        countDownTimer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}