package ru.gb.weather.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HistoryEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val city: String,
    val temperature: Int,
    val condition: String
)