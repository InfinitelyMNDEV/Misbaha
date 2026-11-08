package com.misbaha.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misbaha.app.data.repository.ZikrRepository
import com.misbaha.app.domain.model.ZikrType
import com.misbaha.app.domain.time.AppClock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeZikrItem(
    val id: Long,
    val text: String,
    val type: ZikrType,
    val totalCount: Long,
    val lastUsedAt: Instant?
)

data class HomeUiState(
    val items: List<HomeZikrItem> = emptyList(),
    val now: Instant = Instant.EPOCH,
    val today: LocalDate = LocalDate.MIN,
    val zoneId: ZoneId = ZoneId.systemDefault(),
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val repository: ZikrRepository,
    private val clock: AppClock
) : ViewModel() {
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(TICKER_INTERVAL_MILLIS)
        }
    }

    private val initialState = HomeUiState(
        now = clock.now(),
        today = clock.today(),
        zoneId = clock.zone(),
        isLoading = true
    )

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeAll(),
        ticker
    ) { zikrs, _ ->
        HomeUiState(
            items = zikrs.map { zikr ->
                HomeZikrItem(
                    id = zikr.id,
                    text = zikr.text,
                    type = zikr.type,
                    totalCount = zikr.totalCount,
                    lastUsedAt = zikr.lastUsedAt
                )
            },
            now = clock.now(),
            today = clock.today(),
            zoneId = clock.zone(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = initialState
    )

    fun addCustom(text: String) {
        viewModelScope.launch {
            repository.addCustom(text)
        }
    }

    fun updateCustom(id: Long, text: String) {
        viewModelScope.launch {
            repository.updateCustom(id, text)
        }
    }

    fun deleteCustom(id: Long) {
        viewModelScope.launch {
            repository.deleteCustom(id)
        }
    }

    fun reset(id: Long) {
        viewModelScope.launch {
            repository.reset(id)
        }
    }

    companion object {
        private const val TICKER_INTERVAL_MILLIS = 30_000L
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
