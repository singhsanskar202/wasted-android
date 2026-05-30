package com.wasted.data.model

data class DangerZone(
    val startHour: Int,
    val endHour: Int,
    val level: Level,
    val seconds: Int,
    val appNames: List<String>
) {
    val id: String get() = "${startHour}_${endHour}_${level}"
    enum class Level { CLEAN, LOW, MODERATE, DANGER }
}

data class WeeklyInsight(
    val totalSeconds: List<Int>,
    val dateLabels: List<String>,
    val trend: Trend,
    val verdictLine: String
) {
    enum class Trend { IMPROVING, WORSENING, FLAT }
}

data class InsightResult(
    val zones: List<DangerZone>,
    val timelineSegments: List<DangerZone.Level>,
    val verdictLine: String,
    val tone: Tone,
    val weekly: WeeklyInsight?
) {
    enum class Tone { POSITIVE, NEUTRAL, WARNING }
}
