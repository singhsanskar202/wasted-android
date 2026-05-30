package com.wasted.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.wasted.data.db.AppDatabase
import com.wasted.data.model.DailyUsage
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UsageDaoTest {
    private lateinit var db: AppDatabase

    @Before fun setup() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    @After fun teardown() { db.close() }

    @Test fun `upsert and load today`() = runBlocking {
        val usage = DailyUsage(date = "2026-05-30", seconds = mapOf("a" to 3600), hourly = List(24) { if (it == 10) 3600 else 0 })
        db.usageDao().upsert(usage)
        val loaded = db.usageDao().loadByDate("2026-05-30")
        assertEquals(usage, loaded)
    }

    @Test fun `load missing date returns null`() = runBlocking {
        assertNull(db.usageDao().loadByDate("2000-01-01"))
    }

    @Test fun `load history returns rows sorted ascending`() = runBlocking {
        db.usageDao().upsert(DailyUsage(date = "2026-05-28"))
        db.usageDao().upsert(DailyUsage(date = "2026-05-27"))
        db.usageDao().upsert(DailyUsage(date = "2026-05-29"))
        val history = db.usageDao().loadHistory(excludeDate = "2026-05-30", limit = 7)
        assertEquals(listOf("2026-05-27", "2026-05-28", "2026-05-29"), history.map { it.date })
    }

    @Test fun `upsert overwrites existing row`() = runBlocking {
        db.usageDao().upsert(DailyUsage(date = "2026-05-30", seconds = mapOf("a" to 100)))
        db.usageDao().upsert(DailyUsage(date = "2026-05-30", seconds = mapOf("a" to 200)))
        assertEquals(200, db.usageDao().loadByDate("2026-05-30")?.seconds?.get("a"))
    }
}
