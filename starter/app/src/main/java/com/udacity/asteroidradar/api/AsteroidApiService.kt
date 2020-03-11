package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.IMAGE_BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val moshiRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(IMAGE_BASE_URL)
    .build()

private val scalarRetrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()


interface AsteroidApiService {

    @GET("feed")
    fun getAsteroids(
        @Query("start_date") startDate: String, @Query("end_date") endDate: String, @Query(
            "api_key"
        ) apiKey: String
    ):
            Call<String>

    @GET("apod")
    fun getPictureOrVideoOfTheDayAsync(@Query("api_key") apiKey: String):
            Deferred<PictureOfDay>

}


object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy { scalarRetrofit.create(AsteroidApiService::class.java) }
    val moshiRetrofitService: AsteroidApiService by lazy { moshiRetrofit.create(AsteroidApiService::class.java) }

}
