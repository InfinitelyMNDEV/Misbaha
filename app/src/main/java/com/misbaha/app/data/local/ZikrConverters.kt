package com.misbaha.app.data.local

import androidx.room.TypeConverter
import com.misbaha.app.domain.model.ZikrType

class ZikrConverters {
    @TypeConverter
    fun fromZikrType(value: ZikrType): String = value.name

    @TypeConverter
    fun toZikrType(value: String): ZikrType = ZikrType.valueOf(value)
}
