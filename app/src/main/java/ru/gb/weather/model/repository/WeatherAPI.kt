package ru.gb.weather.model.repository

import retrofit2.*
import retrofit2.http.*
import ru.gb.weather.model.data.WeatherDTO

interface WeatherAPI {
    @GET("v2/informers")
    fun getWeather(
        @Header("X-Yandex-API-Key") token: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Call<WeatherDTO>
}