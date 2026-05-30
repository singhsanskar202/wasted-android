package com.wasted.domain

import com.wasted.data.model.DailyUsage
import com.wasted.data.model.DangerZone
import com.wasted.data.model.InsightResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class InsightEngineTest {

    private fun emptyToday() = DailyUsage(date = "2026-05-30")

    private fun todayWithHourly(vararg pairs: Pair<Int, Int>): DailyUsage {
        val hourly = MutableList(24) { 0 }
        pairs.forEach { (h, s) -> hourly[h] = s }
        return DailyUsage(date = "2026-05-30", hourly = hourly)
    }

    @Test fun `zero usage returns positive tone`() {
        val result = InsightEngine.analyze(emptyToday(), null, emptyList(), emptyMap())
        assertEquals(InsightResult.Tone.POSITIVE, result.tone)
        assertEquals("Clean so far. Come back tonight.", result.verdictLine)
    }

    @Test fun `under 3600s returns positive tone`() {
        val today = todayWithHourly(10 to 1800)
        val result = InsightEngine.analyze(today, null, emptyList(), emptyMap())
        assertEquals(InsightResult.Tone.POSITIVE, result.tone)
    }

    @Test fun `always returns 24 timeline segments`() {
        val result = InsightEngine.analyze(emptyToday(), null, emptyList(), emptyMap())
        assertEquals(24, result.timelineSegments.size)
    }

    @Test fun `danger hour classified correctly`() {
        val today = todayWithHourly(14 to 4000)
        val result = InsightEngine.analyze(today, null, emptyList(), emptyMap())
        assertEquals(DangerZone.Level.DANGER, result.timelineSegments[14])
    }

    @Test fun `moderate hour classified correctly`() {
        val today = todayWithHourly(9 to 2000)
        val result = InsightEngine.analyze(today, null, emptyList(), emptyMap())
        assertEquals(DangerZone.Level.MODERATE, result.timelineSegments[9])
    }

    @Test fun `low hour classified correctly`() {
        val today = todayWithHourly(8 to 600)
        val result = InsightEngine.analyze(today, null, emptyList(), emptyMap())
        assertEquals(DangerZone.Level.LOW, result.timelineSegments[8])
    }

    @Test fun `clean hour classified correctly`() {
        val today = todayWithHourly(8 to 100)
        val result = InsightEngine.analyze(today, null, emptyList(), emptyMap())
        assertEquals(DangerZone.Level.CLEAN, result.timelineSegments[8])
    }

    @Test fun `weekly insight null when fewer than 7 history days`() {
        val today = todayWithHourly(10 to 3600)
        val result = InsightEngine.analyze(today, null, listOf(emptyToday()), emptyMap())
        assertNull(result.weekly)
    }

    @Test fun `weekly insight present when 7 history days`() {
        val history = (1..7).map { DailyUsage(date = "2026-05-${it.toString().padStart(2,'0')}", hourly = List(24){0}) }
        val today = todayWithHourly(10 to 3600)
        val result = InsightEngine.analyze(today, null, history, emptyMap())
        assertNotNull(result.weekly)
        assertEquals(7, result.weekly!!.totalSeconds.size)
    }

    @Test fun `drop vs yesterday fires positive tone`() {
        val yesterday = todayWithHourly(10 to 7200, 11 to 7200)
        val today = todayWithHourly(10 to 1800)
        val result = InsightEngine.analyze(today, yesterday, emptyList(), emptyMap())
        assertEquals(InsightResult.Tone.POSITIVE, result.tone)
    }

    @Test fun `worsening vs yesterday fires warning`() {
        val yesterday = todayWithHourly(10 to 1800)
        val today = todayWithHourly(10 to 3600, 11 to 1800)
        val result = InsightEngine.analyze(today, yesterday, emptyList(), emptyMap())
        assertEquals(InsightResult.Tone.WARNING, result.tone)
    }
}
