package com.perfumevault.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Perfume::class, UsageLog::class, CatalogPerfume::class, PerfumeSize::class],
    version = 11,
    exportSchema = false
)
abstract class PerfumeDatabase : RoomDatabase() {

    abstract fun perfumeDao(): PerfumeDao
    abstract fun usageLogDao(): UsageLogDao
    abstract fun catalogDao(): CatalogDao
    abstract fun perfumeSizeDao(): PerfumeSizeDao

    companion object {
        @Volatile
        private var INSTANCE: PerfumeDatabase? = null

        fun getDatabase(context: Context): PerfumeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PerfumeDatabase::class.java,
                    "perfume_vault.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
