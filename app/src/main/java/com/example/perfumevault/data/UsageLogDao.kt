package com.example.perfumevault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageLogDao {

    @Query("SELECT * FROM usage_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<UsageLog>>

    @Query("SELECT * FROM usage_logs WHERE perfumeId = :perfumeId ORDER BY date DESC")
    fun getLogsForPerfume(perfumeId: Int): Flow<List<UsageLog>>

    @Query("SELECT * FROM usage_logs WHERE date = :date ORDER BY id DESC")
    fun getLogsForDate(date: String): Flow<List<UsageLog>>

    @Query("SELECT COUNT(*) FROM usage_logs WHERE perfumeId = :perfumeId")
    fun getUsageCount(perfumeId: Int): Flow<Int>

    @RewriteQueriesToDropUnusedColumns
    @Query("""
        SELECT p.*, COUNT(l.id) as useCount
        FROM perfumes p
        LEFT JOIN usage_logs l ON p.id = l.perfumeId
        GROUP BY p.id
        ORDER BY useCount DESC
        LIMIT 1
    """)
    fun getMostUsedPerfume(): Flow<Perfume?>

    @Query("SELECT DISTINCT date FROM usage_logs ORDER BY date DESC LIMIT 30")
    fun getRecentDates(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: UsageLog): Long

    @Update
    suspend fun update(log: UsageLog): Int

    @Delete
    suspend fun delete(log: UsageLog): Int

    @Query("DELETE FROM usage_logs WHERE perfumeId = :perfumeId")
    suspend fun deleteAllForPerfume(perfumeId: Int): Int
}
