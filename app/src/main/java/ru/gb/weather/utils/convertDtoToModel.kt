package ru.gb.weather.utils

import ru.gb.weather.model.Weather
import ru.gb.weather.model.data.FactDTO
import ru.gb.weather.model.data.WeatherDTO
import ru.gb.weather.model.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!!
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!))
}