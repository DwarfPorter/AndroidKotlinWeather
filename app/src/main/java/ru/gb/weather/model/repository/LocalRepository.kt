package ru.gb.weather.model.repository

import ru.gb.weather.model.Weather

interface LocalRepository {
    fun getAllHistory() : List<Weather>
    fun saveEntity(weather: Weather)
}