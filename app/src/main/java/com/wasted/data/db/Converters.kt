package com.wasted.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromMap(map: Map<String, Int>): String = Json.encodeToString(map)

    @TypeConverter
    fun toMap(value: String): Map<String, Int> = Json.decodeFromString(value)

    @TypeConverter
    fun fromList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toList(value: String): List<Int> =
        if (value.isEmpty()) emptyList()
        else value.split(",").map { it.toInt() }
}
