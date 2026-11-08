package com.misbaha.app.domain.model

import java.time.Instant
import java.time.LocalDate

data class Zikr(
    val id: Long,
    val text: String,
    val type: ZikrType,
    val position: Int,
    val totalCount: Long,
    val todayCount: Long,
    val todayDate: LocalDate,
    val lastUsedAt: Instant?,
    val phaseIndex: Int,
    val phaseCount: Int
)
