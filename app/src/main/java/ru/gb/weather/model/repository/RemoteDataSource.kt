package ru.gb.weather.model.repository

import okhttp3.*
import ru.gb.weather.BuildConfig

private const val REQUEST_API_KEY = "X-Yandex-API-Key"

class RemoteDataSource {
    fun getWeatherDetails(requestLink: String, callback: Callback) {
        val builder: Request.Builder = Request.Builder().apply {
            header(REQUEST_API_KEY, BuildConfig.WEATHER_API_KEY)
            url(requestLink)
        }
        OkHttpClient().newCall(builder.build()).enqueue(callback)
    }
}