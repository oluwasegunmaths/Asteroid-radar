package com.udacity.asteroidradar

//log
import android.app.Application
import androidx.work.*
import com.udacity.asteroidradar.work.DownloadAsteroidWork
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class AsteroidApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()

        delayedInit()

    }

    private fun delayedInit() = applicationScope.launch {
        setupRecurringWork()

    }

    companion object {
        fun setupRecurringWork() {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresCharging(true)

                .build()

            val repeatingRequest =
                PeriodicWorkRequestBuilder<DownloadAsteroidWork>(1, TimeUnit.DAYS)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                DownloadAsteroidWork.WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                repeatingRequest
            )

        }
    }


}
