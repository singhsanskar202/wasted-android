package com.wasted.domain

import com.wasted.data.model.DailyUsage
import com.wasted.data.model.DangerZone
import com.wasted.data.model.InsightResult
import com.wasted.data.model.WeeklyInsight
import java.text.SimpleDateFormat
import java.util.Locale

object InsightEngine {

    private const val LOW_THRESHOLD      = 300
    private const val MODERATE_THRESHOLD = 1800
    private const val DANGER_THRESHOLD   = 3600
    private const val TREND_THRESHOLD    = 0.15

    fun analyze(
        today: DailyUsage,
        yesterday: DailyUsage?,
        history: List<DailyUsage>,
        displayNames: Map<String, String>
    ): InsightResult {
        val totalToday = today.hourly.sum()
        val segments = today.hourly.map { zoneLevel(it) }
        val zones = buildZones(today.hourly)
        val weekly = if (history.size >= 7) buildWeekly(history) else null

        if (totalToday == 0) return InsightResult(zones, segments, "Clean so far. Come back tonight.", InsightResult.Tone.POSITIVE, weekly)

        val yTotal = yesterday?.hourly?.sum() ?: 0
        if (yTotal > 0 && totalToday < yTotal * (1 - TREND_THRESHOLD * 2)) {
            val pct = ((1 - totalToday.toDouble() / yTotal) * 100).toInt()
            return InsightResult(zones, segments, "$pct% less than yesterday. Actual progress.", InsightResult.Tone.POSITIVE, weekly)
        }

        val yPeak = yesterday?.let { peakHour(it.hourly) }
        if (yPeak != null && today.hourly[yPeak] == 0) {
            return InsightResult(zones, segments, "You skipped the ${hourLabel(yPeak)} habit today.", InsightResult.Tone.POSITIVE, weekly)
        }

        if (totalToday < 3600) return InsightResult(zones, segments, "Under an hour total. That's rare.", InsightResult.Tone.POSITIVE, weekly)

        val dominant = zones.filter { it.level == DangerZone.Level.DANGER || it.level == DangerZone.Level.MODERATE }
            .maxByOrNull { it.seconds }
        if (dominant != null && totalToday > 0 && dominant.seconds.toDouble() / totalToday > 0.5) {
            val label = timeRangeLabel(dominant.startHour, dominant.endHour)
            return InsightResult(zones, segments, "Kill the $label zone and you cut today's waste in half.", InsightResult.Tone.WARNING, weekly)
        }

        val dangerCount = zones.count { it.level == DangerZone.Level.DANGER }
        val streak = longestCleanStreak(today.hourly)
        if (dangerCount >= 3) return InsightResult(zones, segments, "$dangerCount danger zones. No clean run longer than ${streak}h.", InsightResult.Tone.WARNING, weekly)

        if (yTotal > 0 && totalToday > yTotal * (1 + TREND_THRESHOLD)) {
            val pct = ((totalToday.toDouble() / yTotal - 1) * 100).toInt()
            return InsightResult(zones, segments, "$pct% more than yesterday. Trend's going the wrong way.", InsightResult.Tone.WARNING, weekly)
        }

        if (streak >= 4) return InsightResult(zones, segments, "${streak}h clean streak so far. Don't break it.", InsightResult.Tone.NEUTRAL, weekly)

        if (zones.count { it.level != DangerZone.Level.CLEAN } > 4)
            return InsightResult(zones, segments, "Usage is scattered. No single zone to cut — reduce across the board.", InsightResult.Tone.NEUTRAL, weekly)

        val top = zones.filter { it.level != DangerZone.Level.CLEAN }.maxByOrNull { it.seconds }
        if (top != null) {
            val label = timeRangeLabel(top.startHour, top.endHour)
            return InsightResult(zones, segments, "$label is your biggest cost today.", InsightResult.Tone.NEUTRAL, weekly)
        }

        return InsightResult(zones, segments, "Looking fine today.", InsightResult.Tone.POSITIVE, weekly)
    }

    private fun buildWeekly(history: List<DailyUsage>): WeeklyInsight {
        val days = history.takeLast(7)
        val totals = days.map { it.hourly.sum() }
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayFmt = SimpleDateFormat("EEE", Locale.US)
        val labels = days.map { usage ->
            fmt.parse(usage.date)?.let { dayFmt.format(it) } ?: "?"
        }
        val firstHalf = if (totals.size >= 3) totals.take(3).sum() / 3 else 0
        val secondHalf = if (totals.size >= 3) totals.takeLast(3).sum() / 3 else 0
        val trend = when {
            firstHalf == 0 || secondHalf == 0 -> WeeklyInsight.Trend.FLAT
            secondHalf < firstHalf * (1 - TREND_THRESHOLD) -> WeeklyInsight.Trend.IMPROVING
            secondHalf > firstHalf * (1 + TREND_THRESHOLD) -> WeeklyInsight.Trend.WORSENING
            else -> WeeklyInsight.Trend.FLAT
        }
        val verdict = when (trend) {
            WeeklyInsight.Trend.IMPROVING -> {
                val pct = ((1 - secondHalf.toDouble() / firstHalf) * 100).toInt()
                "Down $pct% vs. earlier this week. Keep going."
            }
            WeeklyInsight.Trend.WORSENING -> {
                val pct = ((secondHalf.toDouble() / firstHalf - 1) * 100).toInt()
                "Up $pct% vs. earlier this week. Course-correct now."
            }
            WeeklyInsight.Trend.FLAT -> "Flat week. Pick one zone to cut next week."
        }
        return WeeklyInsight(totals, labels, trend, verdict)
    }

    private fun buildZones(hourly: List<Int>): List<DangerZone> {
        if (hourly.size != 24) return emptyList()
        val zones = mutableListOf<DangerZone>()
        var i = 0
        while (i < 24) {
            val level = zoneLevel(hourly[i])
            var j = i + 1
            while (j < 24 && zoneLevel(hourly[j]) == level) j++
            val secs = hourly.subList(i, j).sum()
            zones.add(DangerZone(startHour = i, endHour = j, level = level, seconds = secs, appNames = emptyList()))
            i = j
        }
        return zones
    }

    private fun zoneLevel(seconds: Int) = when {
        seconds < LOW_THRESHOLD      -> DangerZone.Level.CLEAN
        seconds < MODERATE_THRESHOLD -> DangerZone.Level.LOW
        seconds < DANGER_THRESHOLD   -> DangerZone.Level.MODERATE
        else                         -> DangerZone.Level.DANGER
    }

    private fun peakHour(hourly: List<Int>): Int? {
        val max = hourly.maxOrNull() ?: return null
        return if (max > 0) hourly.indexOf(max) else null
    }

    private fun longestCleanStreak(hourly: List<Int>): Int {
        var best = 0; var current = 0
        for (h in hourly) { if (h < LOW_THRESHOLD) { current++; if (current > best) best = current } else current = 0 }
        return best
    }

    fun timeRangeLabel(start: Int, end: Int) = "${hourLabel(start)}–${hourLabel(end - 1)}"

    fun hourLabel(hour: Int): String {
        val h = if (hour % 12 == 0) 12 else hour % 12
        return "$h${if (hour < 12) "am" else "pm"}"
    }
}
