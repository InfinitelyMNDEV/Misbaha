package com.misbaha.app.domain.time

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

interface AppClock {
    fun now(): Instant
    fun today(): LocalDate
    fun zone(): ZoneId
}

class SystemAppClock(
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : AppClock {
    override fun now(): Instant = Instant.now()
    override fun today(): LocalDate = LocalDate.now(zoneId)
    override fun zone(): ZoneId = zoneId
}
