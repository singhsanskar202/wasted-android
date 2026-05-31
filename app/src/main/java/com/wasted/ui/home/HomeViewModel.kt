package com.wasted.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wasted.data.db.AppDatabase
import com.wasted.data.model.DailyUsage
import com.wasted.data.model.InsightResult
import com.wasted.data.repository.UsageRepository
import com.wasted.domain.EquivalentTaskMapper
import com.wasted.domain.InsightEngine
import com.wasted.domain.QuoteBank
import com.wasted.prefs.WastedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val totalSeconds: Int = 0,
    val daysLeft: Int = 7,
    val today: DailyUsage = DailyUsage(date = DailyUsage.todayString()),
    val insightResult: InsightResult? = null,
    val equivalent: EquivalentTaskMapper.Equivalent? = null,
    val quote: String = "",
    val displayNames: Map<String, String> = emptyMap()
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getInstance(app)
    private val repo = UsageRepository(app, db.usageDao())
    private val prefs = WastedPrefs(app)

    private val _state = MutableStateFlow(HomeUiState(quote = QuoteBank.todaysQuote))
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val displayNames = prefs.displayNames.first()
            val today = repo.loadToday()
            val history = repo.loadHistory()
            val yesterday = repo.loadYesterday()
            val totalSeconds = today.totalSeconds()
            val daysLeft = (7 - history.size).coerceAtLeast(0)
            val insightResult = InsightEngine.analyze(today, yesterday, history, displayNames)
            val equivalent = EquivalentTaskMapper.equivalent(totalSeconds)

            _state.update {
                it.copy(
                    totalSeconds = totalSeconds,
                    daysLeft = daysLeft,
                    today = today,
                    insightResult = insightResult,
                    equivalent = equivalent,
                    displayNames = displayNames
                )
            }
        }
    }
}
