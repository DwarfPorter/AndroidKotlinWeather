package ru.gb.weather.model.data

import ru.gb.weather.model.City
import ru.gb.weather.model.Weather

data class WeatherDTO(
    val fact: FactDTO?
)

fun WeatherDTO.mapTo(city: City): Weather{
    return Weather(city, this.fact?.temp?:0, this.fact?.feels_like?:0, this.fact?.condition?:"")
}