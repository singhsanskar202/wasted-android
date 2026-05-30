package com.wasted.domain

object EquivalentTaskMapper {
    data class Equivalent(val threshold: Int, val description: String, val emoji: String)

    private val table = listOf(
        Equivalent(900,   "a 15-min meditation",       "🧘"),
        Equivalent(1800,  "a 5K run",                  "🏃"),
        Equivalent(3600,  "a book chapter (25 pages)",  "📖"),
        Equivalent(5400,  "cooking a full meal",        "🍳"),
        Equivalent(7200,  "a full workout",             "💪"),
        Equivalent(10800, "watching a short film",      "🎬"),
        Equivalent(14400, "learning a song on guitar",  "🎸"),
        Equivalent(21600, "a half-day hike",            "🥾"),
    )

    fun equivalent(totalSeconds: Int): Equivalent? =
        table.lastOrNull { it.threshold <= totalSeconds }
}
