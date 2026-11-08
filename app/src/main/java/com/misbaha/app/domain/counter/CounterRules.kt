package com.misbaha.app.domain.counter

import java.time.Instant
import java.time.LocalDate

data class CountState(
    val totalCount: Long,
    val todayCount: Long,
    val todayDate: LocalDate,
    val lastUsedAt: Instant?
)

data class ZahraState(
    val totalCycles: Long,
    val todayCycles: Long,
    val todayDate: LocalDate,
    val lastUsedAt: Instant?,
    val phaseIndex: Int,
    val phaseCount: Int
)

enum class ZahraCompletion {
    NONE,
    PHASE,
    CYCLE
}

data class ZahraAdvance(
    val state: ZahraState,
    val completion: ZahraCompletion
)

object ZahraCycle {
    const val TARGET_PER_PHASE = 33
    const val PHASE_COUNT = 3
}

object CounterRules {
    fun recordStandard(current: CountState, today: LocalDate, now: Instant): CountState {
        val todayCount = visibleToday(current.todayCount, current.todayDate, today) + 1L
        return current.copy(
            totalCount = current.totalCount + 1L,
            todayCount = todayCount,
            todayDate = today,
            lastUsedAt = now
        )
    }

    fun recordZahra(current: ZahraState, today: LocalDate, now: Instant): ZahraAdvance {
        val todayCycles = visibleToday(current.todayCycles, current.todayDate, today)
        val nextPhaseCount = current.phaseCount + 1
        if (nextPhaseCount < ZahraCycle.TARGET_PER_PHASE) {
            return ZahraAdvance(
                state = current.copy(
                    todayCycles = todayCycles,
                    todayDate = today,
                    lastUsedAt = now,
                    phaseCount = nextPhaseCount
                ),
                completion = ZahraCompletion.NONE
            )
        }
        val isLastPhase = current.phaseIndex >= ZahraCycle.PHASE_COUNT - 1
        return if (isLastPhase) {
            ZahraAdvance(
                state = current.copy(
                    totalCycles = current.totalCycles + 1L,
                    todayCycles = todayCycles + 1L,
                    todayDate = today,
                    lastUsedAt = now,
                    phaseIndex = 0,
                    phaseCount = 0
                ),
                completion = ZahraCompletion.CYCLE
            )
        } else {
            ZahraAdvance(
                state = current.copy(
                    todayCycles = todayCycles,
                    todayDate = today,
                    lastUsedAt = now,
                    phaseIndex = current.phaseIndex + 1,
                    phaseCount = 0
                ),
                completion = ZahraCompletion.PHASE
            )
        }
    }

    fun visibleToday(count: Long, storedDate: LocalDate, today: LocalDate): Long =
        if (storedDate == today) count else 0L

    fun resetStandard(today: LocalDate): CountState =
        CountState(totalCount = 0L, todayCount = 0L, todayDate = today, lastUsedAt = null)

    fun resetZahra(today: LocalDate): ZahraState =
        ZahraState(
            totalCycles = 0L,
            todayCycles = 0L,
            todayDate = today,
            lastUsedAt = null,
            phaseIndex = 0,
            phaseCount = 0
        )
}
