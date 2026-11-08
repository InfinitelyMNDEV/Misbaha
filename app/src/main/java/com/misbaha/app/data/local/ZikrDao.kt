package com.misbaha.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ZikrDao {
    @Query("SELECT * FROM zikr ORDER BY position ASC, id ASC")
    fun observeAll(): Flow<List<ZikrEntity>>

    @Query("SELECT * FROM zikr WHERE id = :id")
    suspend fun getById(id: Long): ZikrEntity?

    @Query("SELECT * FROM zikr")
    suspend fun getAll(): List<ZikrEntity>

    @Query("SELECT MAX(position) FROM zikr")
    suspend fun maxPosition(): Int?

    @Insert
    suspend fun insert(entity: ZikrEntity): Long

    @Query("UPDATE zikr SET text = :text WHERE id = :id")
    suspend fun updateText(id: Long, text: String)

    @Delete
    suspend fun delete(entity: ZikrEntity)

    @Query(
        "UPDATE zikr SET totalCount = :totalCount, todayCount = :todayCount, todayDate = :todayDate, lastUsedAt = :lastUsedAt WHERE id = :id"
    )
    suspend fun updateStandardState(
        id: Long,
        totalCount: Long,
        todayCount: Long,
        todayDate: String,
        lastUsedAt: Long
    )

    @Query(
        "UPDATE zikr SET totalCount = :totalCount, todayCount = :todayCount, todayDate = :todayDate, lastUsedAt = :lastUsedAt, phaseIndex = :phaseIndex, phaseCount = :phaseCount WHERE id = :id"
    )
    suspend fun updateZahraState(
        id: Long,
        totalCount: Long,
        todayCount: Long,
        todayDate: String,
        lastUsedAt: Long,
        phaseIndex: Int,
        phaseCount: Int
    )

    @Query(
        "UPDATE zikr SET totalCount = 0, todayCount = 0, todayDate = :todayDate, lastUsedAt = NULL, phaseIndex = 0, phaseCount = 0 WHERE id = :id"
    )
    suspend fun resetState(id: Long, todayDate: String)
}
