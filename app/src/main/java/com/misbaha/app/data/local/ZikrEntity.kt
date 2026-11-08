package com.misbaha.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.misbaha.app.domain.model.ZikrType

@Entity(tableName = "zikr")
data class ZikrEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val type: ZikrType,
    val position: Int,
    val totalCount: Long,
    val todayCount: Long,
    val todayDate: String,
    val lastUsedAt: Long?,
    val phaseIndex: Int,
    val phaseCount: Int
)
