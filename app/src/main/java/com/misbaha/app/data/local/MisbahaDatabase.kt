package com.misbaha.app.data.local

import android.content.Context
import androidx.annotation.StringRes
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.misbaha.app.R
import com.misbaha.app.domain.model.ZikrType
import java.time.LocalDate

@Database(
    entities = [ZikrEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(ZikrConverters::class)
abstract class MisbahaDatabase : RoomDatabase() {
    abstract fun zikrDao(): ZikrDao

    companion object {
        private const val DATABASE_NAME = "misbaha.db"

        fun create(context: Context, today: LocalDate): MisbahaDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                MisbahaDatabase::class.java,
                DATABASE_NAME
            )
                .addCallback(BuiltInZikrCallback(context.applicationContext, today))
                .build()
    }
}

private data class BuiltInZikrSeed(
    @StringRes val textResource: Int,
    val type: ZikrType
)

private class BuiltInZikrCallback(
    private val context: Context,
    private val today: LocalDate
) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        val seeds = listOf(
            BuiltInZikrSeed(R.string.zikr_subhan_allah, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_alhamdulillah, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_allahu_akbar, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_la_ilaha_illallah, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_astaghfirullah, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_salawat, ZikrType.STANDARD),
            BuiltInZikrSeed(R.string.zikr_zahra, ZikrType.ZAHRA)
        )
        val todayValue = today.toString()
        seeds.forEachIndexed { index, seed ->
            db.execSQL(
                "INSERT INTO zikr (text, type, position, totalCount, todayCount, todayDate, lastUsedAt, phaseIndex, phaseCount) VALUES (?, ?, ?, 0, 0, ?, NULL, 0, 0)",
                arrayOf<Any>(
                    context.getString(seed.textResource),
                    seed.type.name,
                    index,
                    todayValue
                )
            )
        }
    }
}
