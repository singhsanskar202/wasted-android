package com.wasted.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "daily_usage")
data class DailyUsage(
    @PrimaryKey val date: String,
    val seconds: Map<String, Int> = emptyMap(),
    val hourly: List<Int> = List(24) { 0 }
) {
    fun totalSeconds(): Int = seconds.values.sum()

    companion object {
        private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        fun todayString(): String = fmt.format(Date())
        fun dateString(date: Date): String = fmt.format(date)
    }
}
