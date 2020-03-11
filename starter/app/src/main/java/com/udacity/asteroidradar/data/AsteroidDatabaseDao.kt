package com.udacity.asteroidradar.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(asteroids: List<DatabaseAsteroid>)

    @Query("SELECT * FROM DatabaseAsteroid where closeApproachDate> :timeNowInMillisMinusOneDay Order BY closeApproachDate ASC")
    fun getAllAsteroids(timeNowInMillisMinusOneDay: Long): LiveData<List<DatabaseAsteroid>>
}