package com.udacity.asteroidradar

import android.os.Parcelable
import com.udacity.asteroidradar.data.DatabaseAsteroid
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*


@Parcelize
data class Asteroid(
    val id: Long, val codename: String, val closeApproachDate: String,
    val absoluteMagnitude: Double, val estimatedDiameter: Double,
    val relativeVelocity: Double, val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable {

    fun parseDateStringToLong(closeApproachDate: String): Long {
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val date = try {
            dateFormat.parse(closeApproachDate)

        } catch (e: Exception) {
            null
        }

        return date?.time ?: 0

    }
}

fun List<Asteroid>.asDatabaseModel(): List<DatabaseAsteroid> {

    return map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.parseDateStringToLong(it.closeApproachDate),
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}