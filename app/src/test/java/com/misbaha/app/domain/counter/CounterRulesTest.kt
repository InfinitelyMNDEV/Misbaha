package com.misbaha.app.domain.counter

import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CounterRulesTest {
    private val day = LocalDate.of(2026, 3, 10)
    private val nextDay = LocalDate.of(2026, 3, 11)
    private val now = Instant.parse("2026-03-10T09:15:30Z")

    @Test
    fun standardTotalIsLimitless() {
        var state = CountState(
            totalCount = 0L,
            todayCount = 0L,
            todayDate = day,
            lastUsedAt = null
        )
        repeat(100_000) {
            state = CounterRules.recordStandard(state, day, now)
        }
        assertEquals(100_000L, state.totalCount)
        assertEquals(100_000L, state.todayCount)
    }

    @Test
    fun standardCountResetsOnNewDay() {
        val state = CountState(
            totalCount = 480L,
            todayCount = 32L,
            todayDate = day,
            lastUsedAt = now
        )
        val next = CounterRules.recordStandard(state, nextDay, now)
        assertEquals(481L, next.totalCount)
        assertEquals(1L, next.todayCount)
        assertEquals(nextDay, next.todayDate)
    }

    @Test
    fun visibleTodayKeepsSameDayValue() {
        assertEquals(17L, CounterRules.visibleToday(17L, day, day))
    }

    @Test
    fun visibleTodayDropsPreviousDayValue() {
        assertEquals(0L, CounterRules.visibleToday(17L, day, nextDay))
    }

    @Test
    fun zahraAdvancesPhaseAfterThirtyThreeTaps() {
        var state = ZahraState(
            totalCycles = 0L,
            todayCycles = 0L,
            todayDate = day,
            lastUsedAt = null,
            phaseIndex = 0,
            phaseCount = 0
        )
        var completion = ZahraCompletion.NONE
        repeat(ZahraCycle.TARGET_PER_PHASE) {
            val advance = CounterRules.recordZahra(state, day, now)
            state = advance.state
            completion = advance.completion
        }
        assertEquals(1, state.phaseIndex)
        assertEquals(0, state.phaseCount)
        assertEquals(0L, state.totalCycles)
        assertEquals(ZahraCompletion.PHASE, completion)
    }

    @Test
    fun zahraKeepsInProgressPhaseCount() {
        var state = ZahraState(
            totalCycles = 2L,
            todayCycles = 1L,
            todayDate = day,
            lastUsedAt = null,
            phaseIndex = 1,
            phaseCount = 0
        )
        repeat(10) {
            state = CounterRules.recordZahra(state, day, now).state
        }
        assertEquals(1, state.phaseIndex)
        assertEquals(10, state.phaseCount)
        assertEquals(2L, state.totalCycles)
        assertEquals(1L, state.todayCycles)
    }

    @Test
    fun zahraCompletesCycleAfterNinetyNineTaps() {
        var state = ZahraState(
            totalCycles = 0L,
            todayCycles = 0L,
            todayDate = day,
            lastUsedAt = null,
            phaseIndex = 0,
            phaseCount = 0
        )
        var lastCompletion = ZahraCompletion.NONE
        repeat(ZahraCycle.TARGET_PER_PHASE * ZahraCycle.PHASE_COUNT) {
            val advance = CounterRules.recordZahra(state, day, now)
            state = advance.state
            lastCompletion = advance.completion
        }
        assertEquals(ZahraCompletion.CYCLE, lastCompletion)
        assertEquals(1L, state.totalCycles)
        assertEquals(1L, state.todayCycles)
        assertEquals(0, state.phaseIndex)
        assertEquals(0, state.phaseCount)
    }

    @Test
    fun zahraCompletesCycleOnFinalTapOfLastPhase() {
        val state = ZahraState(
            totalCycles = 4L,
            todayCycles = 2L,
            todayDate = day,
            lastUsedAt = null,
            phaseIndex = ZahraCycle.PHASE_COUNT - 1,
            phaseCount = ZahraCycle.TARGET_PER_PHASE - 1
        )
        val advance = CounterRules.recordZahra(state, day, now)
        assertEquals(ZahraCompletion.CYCLE, advance.completion)
        assertEquals(5L, advance.state.totalCycles)
        assertEquals(3L, advance.state.todayCycles)
        assertEquals(0, advance.state.phaseIndex)
        assertEquals(0, advance.state.phaseCount)
    }

    @Test
    fun zahraProgressSurvivesMidnightWhileTodayCyclesReset() {
        var state = ZahraState(
            totalCycles = 3L,
            todayCycles = 1L,
            todayDate = day,
            lastUsedAt = null,
            phaseIndex = 1,
            phaseCount = 20
        )
        state = CounterRules.recordZahra(state, nextDay, now).state
        assertEquals(1, state.phaseIndex)
        assertEquals(21, state.phaseCount)
        assertEquals(0L, state.todayCycles)
        assertEquals(nextDay, state.todayDate)
    }

    @Test
    fun resetClearsAllCounters() {
        val standard = CounterRules.resetStandard(day)
        assertEquals(0L, standard.totalCount)
        assertEquals(0L, standard.todayCount)
        assertTrue(standard.lastUsedAt == null)

        val zahra = CounterRules.resetZahra(day)
        assertEquals(0L, zahra.totalCycles)
        assertEquals(0L, zahra.todayCycles)
        assertEquals(0, zahra.phaseIndex)
        assertEquals(0, zahra.phaseCount)
        assertTrue(zahra.lastUsedAt == null)
    }
}
