package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.data.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repo.AsteroidRepository
import retrofit2.HttpException

class DownloadAsteroidWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "UploadDataWork"
    }

    override suspend fun doWork(): Result {
        val database = getInstance(applicationContext)
        val repository = AsteroidRepository(database, applicationContext)

        return try {

            repository.refreshAsteroids(true)

            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}