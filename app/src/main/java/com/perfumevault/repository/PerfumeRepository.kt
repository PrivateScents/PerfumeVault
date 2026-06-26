package com.perfumevault.repository

import com.perfumevault.data.*
import kotlinx.coroutines.flow.Flow

class PerfumeRepository(
    private val perfumeDao: PerfumeDao,
    private val logDao: UsageLogDao,
    private val catalogDao: CatalogDao,
    private val sizeDao: PerfumeSizeDao
) {
    // --- Catalog ---
    fun searchCatalog(query: String) = catalogDao.searchCatalog(query)
    suspend fun insertCatalog(perfumes: List<CatalogPerfume>) = catalogDao.insertCatalog(perfumes)
    fun getAllCatalogPerfumes() = catalogDao.getAllCatalogPerfumes()

    // --- Perfumes ---
    val allPerfumes: Flow<List<Perfume>> = perfumeDao.getAllPerfumes()
    val absoluteAllPerfumes: Flow<List<Perfume>> = perfumeDao.getAbsoluteAllPerfumes()
    val wishlistPerfumes: Flow<List<Perfume>> = perfumeDao.getWishlistPerfumes()
    val favorites: Flow<List<Perfume>> = perfumeDao.getFavorites()
    val totalCount: Flow<Int> = perfumeDao.getTotalCount()
    val averageRating: Flow<Double?> = perfumeDao.getAverageRating()
    val totalValue: Flow<Double?> = perfumeDao.getTotalValue()

    fun searchPerfumes(query: String) = perfumeDao.searchPerfumes(query)
    fun getPerfumeById(id: String) = perfumeDao.getPerfumeById(id)
    fun getTopRated(limit: Int = 5) = perfumeDao.getTopRated(limit)

    suspend fun addPerfume(perfume: Perfume) = perfumeDao.insert(perfume)
    suspend fun addPerfumes(perfumes: List<Perfume>) = perfumeDao.insertAll(perfumes)
    suspend fun updatePerfume(perfume: Perfume) = perfumeDao.update(perfume)
    suspend fun deletePerfume(perfume: Perfume) = perfumeDao.delete(perfume)
    suspend fun deleteAllPerfumes() = perfumeDao.deleteAll()
    suspend fun deleteAllSizes() = sizeDao.deleteAll()
    suspend fun toggleFavorite(id: String, current: Boolean) = perfumeDao.setFavorite(id, !current)
    suspend fun updateRemainingMl(id: String, ml: Double) = perfumeDao.updateRemainingMl(id, ml)

    // --- Sizes ---
    fun getSizesForPerfume(id: String) = sizeDao.getSizesForPerfume(id)
    val allSizes: Flow<List<PerfumeSize>> = sizeDao.getAllSizes()
    suspend fun addSizes(sizes: List<PerfumeSize>) = sizeDao.insertSizes(sizes)

    // --- Logs ---
    val allLogs: Flow<List<UsageLog>> = logDao.getAllLogs()
    val recentDates: Flow<List<String>> = logDao.getRecentDates()
    val mostUsed: Flow<Perfume?> = logDao.getMostUsedPerfume()

    fun getLogsForPerfume(id: String) = logDao.getLogsForPerfume(id)
    fun getLogsForDate(date: String) = logDao.getLogsForDate(date)
    fun getUsageCount(id: String) = logDao.getUsageCount(id)

    suspend fun addLog(log: UsageLog) = logDao.insert(log)
    suspend fun addLogs(logs: List<UsageLog>) = logDao.insertAll(logs)
    suspend fun updateLog(log: UsageLog) = logDao.update(log)
    suspend fun deleteLog(log: UsageLog) = logDao.delete(log)
    suspend fun deleteAllLogs() = logDao.deleteAll()
}
