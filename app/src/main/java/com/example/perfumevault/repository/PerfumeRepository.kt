package com.example.perfumevault.repository

import com.example.perfumevault.data.*
import kotlinx.coroutines.flow.Flow

class PerfumeRepository(
    private val perfumeDao: PerfumeDao,
    private val logDao: UsageLogDao
) {
    // --- Perfumes ---
    val allPerfumes: Flow<List<Perfume>> = perfumeDao.getAllPerfumes()
    val wishlistPerfumes: Flow<List<Perfume>> = perfumeDao.getWishlistPerfumes()
    val favorites: Flow<List<Perfume>> = perfumeDao.getFavorites()
    val totalCount: Flow<Int> = perfumeDao.getTotalCount()
    val averageRating: Flow<Double?> = perfumeDao.getAverageRating()
    val totalValue: Flow<Double?> = perfumeDao.getTotalValue()

    fun searchPerfumes(query: String) = perfumeDao.searchPerfumes(query)
    fun getPerfumeById(id: Int) = perfumeDao.getPerfumeById(id)
    fun getTopRated(limit: Int = 5) = perfumeDao.getTopRated(limit)

    suspend fun addPerfume(perfume: Perfume) = perfumeDao.insert(perfume)
    suspend fun addPerfumes(perfumes: List<Perfume>) = perfumeDao.insertAll(perfumes)
    suspend fun updatePerfume(perfume: Perfume) = perfumeDao.update(perfume)
    suspend fun deletePerfume(perfume: Perfume) = perfumeDao.delete(perfume)
    suspend fun toggleFavorite(id: Int, current: Boolean) = perfumeDao.setFavorite(id, !current)
    suspend fun updateRemainingMl(id: Int, ml: Double) = perfumeDao.updateRemainingMl(id, ml)

    // --- Logs ---
    val allLogs: Flow<List<UsageLog>> = logDao.getAllLogs()
    val recentDates: Flow<List<String>> = logDao.getRecentDates()
    val mostUsed: Flow<Perfume?> = logDao.getMostUsedPerfume()

    fun getLogsForPerfume(id: Int) = logDao.getLogsForPerfume(id)
    fun getLogsForDate(date: String) = logDao.getLogsForDate(date)
    fun getUsageCount(id: Int) = logDao.getUsageCount(id)

    suspend fun addLog(log: UsageLog) = logDao.insert(log)
    suspend fun updateLog(log: UsageLog) = logDao.update(log)
    suspend fun deleteLog(log: UsageLog) = logDao.delete(log)
}
