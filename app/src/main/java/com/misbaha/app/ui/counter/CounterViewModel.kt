package com.misbaha.app.ui.counter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misbaha.app.data.repository.ZikrRepository
import com.misbaha.app.domain.counter.CounterRules
import com.misbaha.app.domain.counter.CountState
import com.misbaha.app.domain.counter.ZahraAdvance
import com.misbaha.app.domain.counter.ZahraCompletion
import com.misbaha.app.domain.counter.ZahraCycle
import com.misbaha.app.domain.counter.ZahraState
import com.misbaha.app.domain.model.ZikrType
import com.misbaha.app.domain.time.AppClock
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CounterUiState(
    val isLoading: Boolean = true,
    val isMissing: Boolean = false,
    val id: Long = 0L,
    val text: String = "",
    val type: ZikrType = ZikrType.STANDARD,
    val totalCount: Long = 0L,
    val todayCount: Long = 0L,
    val todayDate: LocalDate = LocalDate.MIN,
    val phaseIndex: Int = 0,
    val phaseCount: Int = 0
)

class CounterViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ZikrRepository,
    private val clock: AppClock
) : ViewModel() {
    private val zikrId: Long = savedStateHandle.get<Long>(ZIKR_ID_ARGUMENT) ?: 0L

    private val _uiState = MutableStateFlow(CounterUiState(todayDate = clock.today()))
    val uiState: StateFlow<CounterUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ZahraCompletion>(extraBufferCapacity = EVENT_BUFFER_CAPACITY)
    val events: SharedFlow<ZahraCompletion> = _events.asSharedFlow()

    private val tapChannel = Channel<Tap>(Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            val zikr = repository.get(zikrId)
            if (zikr == null) {
                _uiState.value = CounterUiState(isLoading = false, isMissing = true)
            } else {
                val today = clock.today()
                _uiState.value = CounterUiState(
                    isLoading = false,
                    id = zikr.id,
                    text = zikr.text,
                    type = zikr.type,
                    totalCount = zikr.totalCount,
                    todayCount = CounterRules.visibleToday(zikr.todayCount, zikr.todayDate, today),
                    todayDate = today,
                    phaseIndex = zikr.phaseIndex.coerceIn(0, ZahraCycle.PHASE_COUNT - 1),
                    phaseCount = zikr.phaseCount.coerceIn(0, ZahraCycle.TARGET_PER_PHASE - 1)
                )
            }
        }
        startTapWriter()
        startMidnightTicker()
    }

    fun onTap() {
        val current = _uiState.value
        if (current.isLoading || current.isMissing) {
            return
        }
        val today = clock.today()
        val now = clock.now()
        if (current.type == ZikrType.ZAHRA) {
            val advance: ZahraAdvance = CounterRules.recordZahra(current.toZahraState(), today, now)
            _uiState.value = current.copy(
                totalCount = advance.state.totalCycles,
                todayCount = advance.state.todayCycles,
                todayDate = advance.state.todayDate,
                phaseIndex = advance.state.phaseIndex,
                phaseCount = advance.state.phaseCount
            )
            if (advance.completion != ZahraCompletion.NONE) {
                _events.tryEmit(advance.completion)
            }
        } else {
            val next = CounterRules.recordStandard(current.toCountState(), today, now)
            _uiState.value = current.copy(
                totalCount = next.totalCount,
                todayCount = next.todayCount,
                todayDate = next.todayDate
            )
        }
        tapChannel.trySend(Tap(today = today, now = now))
    }

    private fun startTapWriter() {
        viewModelScope.launch {
            for (tap in tapChannel) {
                runCatching {
                    repository.recordTap(zikrId, tap.today, tap.now)
                }
            }
        }
    }

    private fun startMidnightTicker() {
        viewModelScope.launch {
            while (true) {
                val zone = clock.zone()
                val nextMidnight = clock.today().plusDays(1).atStartOfDay(zone).toInstant()
                val delayMillis = Duration.between(clock.now(), nextMidnight)
                    .toMillis()
                    .coerceAtLeast(MIN_MIDNIGHT_DELAY_MILLIS)
                delay(delayMillis)
                val today = clock.today()
                _uiState.update { state ->
                    if (state.todayDate != today) {
                        state.copy(todayCount = 0L, todayDate = today)
                    } else {
                        state
                    }
                }
            }
        }
    }

    companion object {
        const val ZIKR_ID_ARGUMENT = "zikrId"
        private const val EVENT_BUFFER_CAPACITY = 4
        private const val MIN_MIDNIGHT_DELAY_MILLIS = 1_000L
    }
}

private data class Tap(
    val today: LocalDate,
    val now: Instant
)

private fun CounterUiState.toCountState(): CountState = CountState(
    totalCount = totalCount,
    todayCount = todayCount,
    todayDate = todayDate,
    lastUsedAt = null
)

private fun CounterUiState.toZahraState(): ZahraState = ZahraState(
    totalCycles = totalCount,
    todayCycles = todayCount,
    todayDate = todayDate,
    lastUsedAt = null,
    phaseIndex = phaseIndex,
    phaseCount = phaseCount
)
