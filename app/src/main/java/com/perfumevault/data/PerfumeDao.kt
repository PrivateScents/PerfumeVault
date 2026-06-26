package com.perfumevault.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfumeDao {

    // --- Lesen ---
    @Query("SELECT * FROM perfumes WHERE is_wishlist = 0 ORDER BY brand ASC, name ASC")
    fun getAllPerfumes(): Flow<List<Perfume>>

    @Query("SELECT * FROM perfumes")
    fun getAbsoluteAllPerfumes(): Flow<List<Perfume>>

    @Query("SELECT * FROM perfumes WHERE is_wishlist = 1 ORDER BY brand ASC, name ASC")
    fun getWishlistPerfumes(): Flow<List<Perfume>>

    @Query("SELECT * FROM perfumes WHERE id = :id")
    fun getPerfumeById(id: String): Flow<Perfume?>

    @Query("SELECT * FROM perfumes WHERE is_favourite = 1 AND is_wishlist = 0 ORDER BY rating DESC")
    fun getFavorites(): Flow<List<Perfume>>

    @Query("""
        SELECT * FROM perfumes 
        WHERE is_wishlist = 0 
        AND (name LIKE '%' || :query || '%' 
             OR brand LIKE '%' || :query || '%' 
             OR brand || ' ' || name LIKE '%' || :query || '%'
             OR name || ' ' || brand LIKE '%' || :query || '%'
             OR type LIKE '%' || :query || '%' 
             OR notes LIKE '%' || :query || '%')
        ORDER BY brand ASC
    """)
    fun searchPerfumes(query: String): Flow<List<Perfume>>

    @Query("SELECT * FROM perfumes WHERE is_wishlist = 0 ORDER BY rating DESC LIMIT :limit")
    fun getTopRated(limit: Int = 10): Flow<List<Perfume>>

    @Query("SELECT COUNT(*) FROM perfumes WHERE is_wishlist = 0")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT AVG(rating) FROM perfumes WHERE is_wishlist = 0")
    fun getAverageRating(): Flow<Double?>

    @Query("SELECT SUM(price) FROM perfumes WHERE is_wishlist = 0")
    fun getTotalValue(): Flow<Double?>

    // --- Schreiben ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(perfume: Perfume)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(perfumes: List<Perfume>)

    @Update
    suspend fun update(perfume: Perfume): Int

    @Delete
    suspend fun delete(perfume: Perfume): Int

    @Query("DELETE FROM perfumes")
    suspend fun deleteAll(): Int

    @Query("UPDATE perfumes SET is_favourite = :fav WHERE id = :id")
    suspend fun setFavorite(id: String, fav: Boolean): Int

    @Query("UPDATE perfumes SET remainingMl = :ml WHERE id = :id")
    suspend fun updateRemainingMl(id: String, ml: Double): Int
}
