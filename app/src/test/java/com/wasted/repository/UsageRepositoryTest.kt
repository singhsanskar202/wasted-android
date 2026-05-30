package com.wasted.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.wasted.data.db.AppDatabase
import com.wasted.data.model.DailyUsage
import com.wasted.data.repository.UsageRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UsageRepositoryTest {
    private lateinit var db: AppDatabase
    private lateinit var repo: UsageRepository

    @Before fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries().build()
        repo = UsageRepository(ctx, db.usageDao())
    }

    @After fun teardown() { db.close() }

    @Test fun `loadToday returns empty DailyUsage when no data`() = runBlocking {
        val today = repo.loadToday()
        assertEquals(DailyUsage.todayString(), today.date)
        assertTrue(today.seconds.isEmpty())
    }

    @Test fun `loadHistory returns empty list when no data`() = runBlocking {
        assertTrue(repo.loadHistory().isEmpty())
    }

    @Test fun `upsert and reload today`() = runBlocking {
        val usage = DailyUsage(date = DailyUsage.todayString(), seconds = mapOf("com.instagram.android" to 3600))
        repo.upsert(usage)
        assertEquals(3600, repo.loadToday().seconds["com.instagram.android"])
    }
}
