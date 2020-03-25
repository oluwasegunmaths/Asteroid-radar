package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.data.AsteroidDatabase.Companion.getInstance
import com.udacity.asteroidradar.repo.AsteroidRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class AsteroidApiStatus { LOADING, NO_NETWORK, ERROR, DONE }
    enum class AsteroidFilter() { SHOW_WEEK, SHOW_TODAY, SHOW_ALL }

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()

    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val viewModelJob = SupervisorJob()

    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val database = getInstance(application)
    private val asteroidRepository = AsteroidRepository(database, application.applicationContext)


    init {
        viewModelScope.launch {

            _pictureOfDay.value = asteroidRepository.getPictureOfTheDay()

            asteroidRepository.refreshAsteroids(false)

        }

    }

    var response = asteroidRepository.getAsteroidsByFilter(AsteroidFilter.SHOW_WEEK)

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidFilter) {
        response = asteroidRepository.getAsteroidsByFilter(filter)
    }

    companion object {
        val status = MutableLiveData<AsteroidApiStatus>()
    }
}
