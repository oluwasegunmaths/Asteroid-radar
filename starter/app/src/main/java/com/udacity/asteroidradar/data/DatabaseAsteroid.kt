package com.udacity.asteroidradar.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
@Entity
data class DatabaseAsteroid(
    @PrimaryKey
    val id: Long,
    val codename: String,
    val closeApproachDate: Long,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
) : Parcelable {
    fun parseDateLongToStringFormatted(closeApproachDate: Long): String {

        return try {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = closeApproachDate
            val setTime = calendar.time

            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

            dateFormat.format(setTime)
        } catch (e: Exception) {
            ""
        }
    }
}

fun List<DatabaseAsteroid>.asDomainModel(): List<Asteroid> {

    return map {
        Asteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.parseDateLongToStringFormatted(it.closeApproachDate),
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }
}