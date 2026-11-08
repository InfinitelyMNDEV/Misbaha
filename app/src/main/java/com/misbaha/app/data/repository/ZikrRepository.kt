package com.misbaha.app.data.repository

import androidx.room.withTransaction
import com.misbaha.app.data.local.MisbahaDatabase
import com.misbaha.app.data.local.ZikrDao
import com.misbaha.app.data.local.ZikrEntity
import com.misbaha.app.domain.counter.CounterRules
import com.misbaha.app.domain.counter.CountState
import com.misbaha.app.domain.counter.ZahraCompletion
import com.misbaha.app.domain.counter.ZahraState
import com.misbaha.app.domain.model.Zikr
import com.misbaha.app.domain.model.ZikrType
import com.misbaha.app.domain.time.AppClock
import com.misbaha.app.domain.validation.ZikrTextValidation
import com.misbaha.app.domain.validation.ZikrTextValidator
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

sealed interface ZikrWriteOutcome {
    data class Success(val id: Long) : ZikrWriteOutcome
    data object EmptyText : ZikrWriteOutcome
    data object DuplicateText : ZikrWriteOutcome
    data object NotFound : ZikrWriteOutcome
}

data class TapResult(
    val zikr: Zikr,
    val completion: ZahraCompletion
)

class ZikrRepository(
    private val dao: ZikrDao,
    private val database: MisbahaDatabase,
    private val clock: AppClock
) {
    fun observeAll(): Flow<List<Zikr>> =
        dao.observeAll().map { entities -> entities.map(ZikrEntity::toDomain) }

    suspend fun get(id: Long): Zikr? = dao.getById(id)?.toDomain()

    suspend fun addCustom(rawText: String): ZikrWriteOutcome = database.withTransaction {
        val existingTexts = dao.getAll().map { it.text }
        when (val validation = ZikrTextValidator.validate(rawText, existingTexts)) {
            ZikrTextValidation.Empty -> ZikrWriteOutcome.EmptyText
            ZikrTextValidation.Duplicate -> ZikrWriteOutcome.DuplicateText
            is ZikrTextValidation.Valid -> {
                val today = clock.today()
                val id = dao.insert(
                    ZikrEntity(
                        text = validation.text,
                        type = ZikrType.CUSTOM,
                        position = (dao.maxPosition() ?: -1) + 1,
                        totalCount = 0L,
                        todayCount = 0L,
                        todayDate = today.toString(),
                        lastUsedAt = null,
                        phaseIndex = 0,
                        phaseCount = 0
                    )
                )
                ZikrWriteOutcome.Success(id)
            }
        }
    }

    suspend fun updateCustom(id: Long, rawText: String): ZikrWriteOutcome = database.withTransaction {
        val entity = dao.getById(id)
        if (entity == null || entity.type != ZikrType.CUSTOM) {
            return@withTransaction ZikrWriteOutcome.NotFound
        }
        val existingTexts = dao.getAll().filter { it.id != id }.map { it.text }
        when (val validation = ZikrTextValidator.validate(rawText, existingTexts)) {
            ZikrTextValidation.Empty -> ZikrWriteOutcome.EmptyText
            ZikrTextValidation.Duplicate -> ZikrWriteOutcome.DuplicateText
            is ZikrTextValidation.Valid -> {
                dao.updateText(id, validation.text)
                ZikrWriteOutcome.Success(id)
            }
        }
    }

    suspend fun deleteCustom(id: Long): Boolean = database.withTransaction {
        val entity = dao.getById(id)
        if (entity == null || entity.type != ZikrType.CUSTOM) {
            return@withTransaction false
        }
        dao.delete(entity)
        true
    }

    suspend fun reset(id: Long) {
        dao.resetState(id, clock.today().toString())
    }

    suspend fun recordTap(id: Long, today: LocalDate, now: Instant): TapResult? =
        database.withTransaction {
            val entity = dao.getById(id) ?: return@withTransaction null
            if (entity.type == ZikrType.ZAHRA) {
                val advance = CounterRules.recordZahra(entity.toZahraState(), today, now)
                val lastUsedAt = now.toEpochMilli()
                dao.updateZahraState(
                    id = id,
                    totalCount = advance.state.totalCycles,
                    todayCount = advance.state.todayCycles,
                    todayDate = advance.state.todayDate.toString(),
                    lastUsedAt = lastUsedAt,
                    phaseIndex = advance.state.phaseIndex,
                    phaseCount = advance.state.phaseCount
                )
                TapResult(
                    zikr = entity.copy(
                        totalCount = advance.state.totalCycles,
                        todayCount = advance.state.todayCycles,
                        todayDate = advance.state.todayDate.toString(),
                        lastUsedAt = lastUsedAt,
                        phaseIndex = advance.state.phaseIndex,
                        phaseCount = advance.state.phaseCount
                    ).toDomain(),
                    completion = advance.completion
                )
            } else {
                val next = CounterRules.recordStandard(entity.toCountState(), today, now)
                val lastUsedAt = now.toEpochMilli()
                dao.updateStandardState(
                    id = id,
                    totalCount = next.totalCount,
                    todayCount = next.todayCount,
                    todayDate = next.todayDate.toString(),
                    lastUsedAt = lastUsedAt
                )
                TapResult(
                    zikr = entity.copy(
                        totalCount = next.totalCount,
                        todayCount = next.todayCount,
                        todayDate = next.todayDate.toString(),
                        lastUsedAt = lastUsedAt
                    ).toDomain(),
                    completion = ZahraCompletion.NONE
                )
            }
        }
}

private fun ZikrEntity.toDomain(): Zikr = Zikr(
    id = id,
    text = text,
    type = type,
    position = position,
    totalCount = totalCount,
    todayCount = todayCount,
    todayDate = LocalDate.parse(todayDate),
    lastUsedAt = lastUsedAt?.let(Instant::ofEpochMilli),
    phaseIndex = phaseIndex,
    phaseCount = phaseCount
)

private fun ZikrEntity.toCountState(): CountState = CountState(
    totalCount = totalCount,
    todayCount = todayCount,
    todayDate = LocalDate.parse(todayDate),
    lastUsedAt = lastUsedAt?.let(Instant::ofEpochMilli)
)

private fun ZikrEntity.toZahraState(): ZahraState = ZahraState(
    totalCycles = totalCount,
    todayCycles = todayCount,
    todayDate = LocalDate.parse(todayDate),
    lastUsedAt = lastUsedAt?.let(Instant::ofEpochMilli),
    phaseIndex = phaseIndex,
    phaseCount = phaseCount
)
