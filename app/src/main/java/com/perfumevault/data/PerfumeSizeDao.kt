package com.perfumevault.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfumeSizeDao {
    @Query("SELECT * FROM perfume_sizes WHERE perfume_id = :perfumeId")
    fun getSizesForPerfume(perfumeId: String): Flow<List<PerfumeSize>>

    @Query("SELECT * FROM perfume_sizes")
    fun getAllSizes(): Flow<List<PerfumeSize>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSize(size: PerfumeSize)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSizes(sizes: List<PerfumeSize>)

    @Query("DELETE FROM perfume_sizes")
    suspend fun deleteAll(): Int
}
