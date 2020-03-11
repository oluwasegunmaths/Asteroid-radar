package com.udacity.asteroidradar.repo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.MILLIS_IN_A_DAY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getTodayFormattedDate
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.asDatabaseModel
import com.udacity.asteroidradar.data.AsteroidDatabase
import com.udacity.asteroidradar.data.asDomainModel
import com.udacity.asteroidradar.main.MainViewModel
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AsteroidRepository(
    private val database: AsteroidDatabase,
    private val context: Context
) {
    private var localDatabaseIsNotEmpty: Boolean = false

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDatabaseDao.getAllAsteroids(
            Calendar.getInstance().timeInMillis - MILLIS_IN_A_DAY /* subtract milliseconds in a day to include today's asteroids in cursor returned by query */
        )
    ) {
        if (it != null && it.isNotEmpty()) {
            MainViewModel.status.value = MainViewModel.AsteroidApiStatus.DONE
            localDatabaseIsNotEmpty = true
        }
        it.asDomainModel()

    }
    private var job = Job()

    private val uiScope = CoroutineScope(job + Dispatchers.Main)


    suspend fun refreshAsteroids() {
//        if getconnectiontype method returns 1 or 2, then there is network
        if (getConnectionType(context) != 0) {
            uiScope.launch {
                if (!localDatabaseIsNotEmpty) {
                    MainViewModel.status.value = MainViewModel.AsteroidApiStatus.LOADING
                }
                AsteroidApi.retrofitService.getAsteroids(
                    getTodayFormattedDate(false), getTodayFormattedDate(true),
                    Constants.API_KEY
                ).enqueue(object : Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        if (!localDatabaseIsNotEmpty) {
                            MainViewModel.status.value = MainViewModel.AsteroidApiStatus.ERROR
                        }
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {

                        response.body()?.let {

                            val asteroidJSON = JSONObject(it)
                            val asteroidList = parseAsteroidsJsonResult(asteroidJSON)
                            uiScope.launch {

                                withContext(Dispatchers.IO) {

                                    database.asteroidDatabaseDao.bulkInsert(asteroidList.asDatabaseModel())

                                }

                            }
                            MainViewModel.status.value = MainViewModel.AsteroidApiStatus.DONE
                        }
                    }
                })

            }
        } else {
            if (!localDatabaseIsNotEmpty) {
                MainViewModel.status.value = MainViewModel.AsteroidApiStatus.NO_NETWORK
            }
        }
    }

    suspend fun getPictureOfTheDay(): PictureOfDay? {
        return withContext(Dispatchers.IO) {
            //        if getconnectiontype method returns 1 or 2, then there is network
            if (getConnectionType(context) != 0) {
                try {
                    var getPictureOfDay: PictureOfDay? =
                        AsteroidApi.moshiRetrofitService.getPictureOrVideoOfTheDayAsync(Constants.API_KEY)
                            .await()
                    val isImage =
                        getPictureOfDay?.mediaType?.matches(Constants.IMAGE.toRegex()) ?: false
                    if (!isImage) {
                        getPictureOfDay = null
                    }
                    getPictureOfDay
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }


    // convenient method to detect whether there is connection
//   @return 0: No Internet available (maybe on airplane mode, or in the process of joining an wi-fi).
//
//   @return 1: Cellular (mobile data, 3G/4G/LTE whatever).
//
//    @return 2: Wi-fi.
    private fun getConnectionType(context: Context): Int {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        result = 2
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        result = 1
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    if (type == ConnectivityManager.TYPE_WIFI) {
                        result = 2
                    } else if (type == ConnectivityManager.TYPE_MOBILE) {
                        result = 1
                    }
                }
            }
        }
        return result
    }
}
