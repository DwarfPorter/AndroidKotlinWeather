package ru.gb.weather.model.repository

import ru.gb.weather.model.Weather
import ru.gb.weather.model.room.HistoryDao
import ru.gb.weather.utils.convertHistoryEntityToWeather
import ru.gb.weather.utils.convertWeatherToEntity

class LocalRepositoryImpl (private val localDataSource : HistoryDao) : LocalRepository{
    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }

    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToEntity(weather))
    }
}