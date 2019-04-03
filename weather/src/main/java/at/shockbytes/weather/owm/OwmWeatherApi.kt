package at.shockbytes.weather.owm

import io.reactivex.Single

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Author:  Martin Macheiner
 * Date:    21.09.2017
 */
interface OwmWeatherApi {

    @GET("forecast")
    fun getWeatherForecast(
        @Query("q") place: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Single<OwmWeatherForecast>

    @GET("weather")
    fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lng: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Single<OwmCurrentWeather>

    companion object {

        const val SERVICE_ENDPOINT = "https://api.openweathermap.org/data/2.5/"

        const val API_KEY = "f1a5564dfc20059ace98a12dafc792d8"
    }
}