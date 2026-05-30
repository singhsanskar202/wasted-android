package com.wasted.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_usage")
data class DailyUsage(
    @PrimaryKey val date: String,
    val seconds: Map<String, Int> = emptyMap(),
    val hourly: List<Int> = List(24) { 0 }
) {
    fun totalSeconds(): Int = seconds.values.sum()

    companion object {
        fun todayString(): String = java.time.LocalDate.now().toString()
        fun dateString(date: Date): String =
            date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString()
    }
}
