package ru.gb.weather.utils

import ru.gb.weather.model.City
import ru.gb.weather.model.Weather
import ru.gb.weather.model.data.FactDTO
import ru.gb.weather.model.data.WeatherDTO
import ru.gb.weather.model.getDefaultCity
import ru.gb.weather.model.room.HistoryEntity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!))
}

fun convertHistoryEntityToWeather(entityList: List<HistoryEntity>): List<Weather> {
    return entityList.map {
        Weather(City(it.city, 0.0, 0.0), it.temperature, 0, it.condition)
    }
}

fun convertWeatherToEntity(weather: Weather): HistoryEntity {
    return HistoryEntity(0, weather.city.city, weather.temperature, weather.condition)
}
