package com.wasted.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.wasted.data.model.DailyUsage

@Dao
interface UsageDao {
    @Upsert
    suspend fun upsert(usage: DailyUsage)

    @Query("SELECT * FROM daily_usage WHERE date = :date LIMIT 1")
    suspend fun loadByDate(date: String): DailyUsage?

    @Query("SELECT * FROM daily_usage WHERE date < :excludeDate ORDER BY date DESC LIMIT :limit")
    suspend fun loadHistory(excludeDate: String, limit: Int): List<DailyUsage>

    @Query("DELETE FROM daily_usage WHERE date < :cutoffDate")
    suspend fun deleteBefore(cutoffDate: String)
}
