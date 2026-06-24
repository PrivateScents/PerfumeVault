package com.perfumevault.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {
    @Query("SELECT * FROM perfume_catalog ORDER BY brand ASC, name ASC")
    fun getAllCatalogPerfumes(): Flow<List<CatalogPerfume>>

    @Query("""
        SELECT * FROM perfume_catalog 
        WHERE name LIKE '%' || :query || '%' 
           OR brand LIKE '%' || :query || '%' 
           OR brand || ' ' || name LIKE '%' || :query || '%'
           OR name || ' ' || brand LIKE '%' || :query || '%'
        ORDER BY brand ASC
    """)
    fun searchCatalog(query: String): Flow<List<CatalogPerfume>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatalog(perfumes: List<CatalogPerfume>)
}
