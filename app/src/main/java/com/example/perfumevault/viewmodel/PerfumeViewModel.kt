package com.example.perfumevault.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.*
import com.example.perfumevault.data.Perfume
import com.example.perfumevault.data.UsageLog
import com.example.perfumevault.repository.PerfumeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.LocalDate
import kotlin.math.round

@OptIn(ExperimentalCoroutinesApi::class)
class PerfumeViewModel(
    private val application: Application,
    private val repo: PerfumeRepository
) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("perfume_vault_prefs", Context.MODE_PRIVATE)

    // --- UI State ---
    private val _currentLanguage = MutableStateFlow(prefs.getString("language", "de") ?: "de")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("is_dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _sortMode = MutableStateFlow(
        runCatching { SortMode.valueOf(prefs.getString("sort_mode", "BRAND") ?: "BRAND") }.getOrDefault(SortMode.BRAND)
    )
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    private val _filterFavorites = MutableStateFlow(false)
    val filterFavorites: StateFlow<Boolean> = _filterFavorites.asStateFlow()

    private val _filterSeason = MutableStateFlow(prefs.getString("filter_season", "Alle") ?: "Alle")
    val filterSeason: StateFlow<String> = _filterSeason.asStateFlow()

    // --- Localization Helper ---
    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
        prefs.edit { putString("language", lang) }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit { putBoolean("is_dark_mode", enabled) }
    }

    fun t(de: String, en: String): String {
        return if (_currentLanguage.value == "de") de else en
    }

    fun translateSeason(season: String): String {
        val list = season.split(" / ").filter { it.isNotBlank() }
        if (list.isEmpty()) return t("Alle", "All")
        return list.joinToString(" / ") { s ->
            when(s.trim()) {
                "Alle", "All" -> t("Alle", "All")
                "Frühling", "Spring" -> t("Frühling", "Spring")
                "Sommer", "Summer" -> t("Sommer", "Summer")
                "Herbst", "Autumn" -> t("Herbst", "Autumn")
                "Winter" -> t("Winter", "Winter")
                else -> s
            }
        }
    }

    fun translateOccasion(occasion: String): String {
        val list = occasion.split(" / ").filter { it.isNotBlank() }
        if (list.isEmpty()) return t("Alle", "All")
        return list.joinToString(" / ") { o ->
            when(o.trim()) {
                "Alle", "All" -> t("Alle", "All")
                "Alltag", "Daily" -> t("Alltag", "Daily")
                "Büro", "Office" -> t("Büro", "Office")
                "Business" -> t("Business", "Business")
                "Abend", "Evening" -> t("Abend", "Evening")
                "Ausgehen", "Going Out" -> t("Ausgehen", "Going Out")
                "Sport" -> t("Sport", "Sport")
                "Reise", "Travel" -> t("Reise", "Travel")
                "Date" -> t("Date", "Date")
                "Besonderer Anlass", "Special Occasion" -> t("Besonderer Anlass", "Special Occasion")
                else -> o
            }
        }
    }

    // --- Daten ---
    val allPerfumes: StateFlow<List<Perfume>> = combine(
        _searchQuery.flatMapLatest { if (it.isBlank()) repo.allPerfumes else repo.searchPerfumes(it) },
        _sortMode,
        _filterFavorites,
        _filterSeason
    ) { list, sort, favOnly, season ->
        var filtered = if (favOnly) list.filter { it.isFavorite } else list
        if (season != "Alle") {
            filtered = filtered.filter { it.season.contains(season) || it.season == "Alle" }
        }
        sort.apply(filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = repo.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageRating: StateFlow<Double> = repo.averageRating
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalValue: StateFlow<Double> = repo.totalValue
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allLogs: StateFlow<List<UsageLog>> = repo.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mostUsed: StateFlow<Perfume?> = repo.mostUsed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val unfilteredPerfumes: StateFlow<List<Perfume>> = repo.allPerfumes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistPerfumes: StateFlow<List<Perfume>> = combine(
        repo.wishlistPerfumes,
        _searchQuery,
        _filterSeason
    ) { list, query, season ->
        var filtered = if (query.isBlank()) list 
        else list.filter { it.name.contains(query, true) || it.brand.contains(query, true) }
        if (season != "Alle") {
            filtered = filtered.filter { it.season.contains(season) || it.season == "Alle" }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Actions ---
    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setTab(tab: Int) { _selectedTab.value = tab }
    fun setSortMode(mode: SortMode) { 
        _sortMode.value = mode 
        prefs.edit { putString("sort_mode", mode.name) }
    }
    fun toggleFavoriteFilter() { _filterFavorites.value = !_filterFavorites.value }

    fun setFilterSeason(season: String) {
        _filterSeason.value = season
        prefs.edit { putString("filter_season", season) }
    }

    fun addPerfume(
        name: String, brand: String, rating: Double, type: String,
        concentration: String, season: String, occasion: String,
        bottleSize: Int, remainingMl: Double, price: Double, notes: String, imageUrl: String,
        isWishlist: Boolean = false
    ) {
        viewModelScope.launch {
            repo.addPerfume(Perfume(
                name = name, brand = brand, rating = rating, type = type,
                concentration = concentration, season = season, occasion = occasion,
                bottleSize = bottleSize,
                remainingMl = remainingMl,
                price = price, notes = notes,
                imageUrl = imageUrl,
                addedDate = LocalDate.now().toString(),
                isWishlist = isWishlist
            ))
        }
    }

    fun moveToCollection(perfume: Perfume) {
        viewModelScope.launch {
            repo.updatePerfume(perfume.copy(isWishlist = false, addedDate = LocalDate.now().toString()))
        }
    }

    fun updatePerfume(perfume: Perfume) {
        viewModelScope.launch { repo.updatePerfume(perfume) }
    }

    fun deletePerfume(perfume: Perfume) {
        viewModelScope.launch { repo.deletePerfume(perfume) }
    }

    fun toggleFavorite(perfume: Perfume) {
        viewModelScope.launch { repo.toggleFavorite(perfume.id, perfume.isFavorite) }
    }

    fun updateRemainingMl(id: Int, ml: Double) {
        viewModelScope.launch { repo.updateRemainingMl(id, ml) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repo.deleteAllLogs()
            repo.deleteAllPerfumes()
        }
    }

    fun addPerfumesFromText(text: String) {
        viewModelScope.launch {
            val perfumes = mutableListOf<Perfume>()
            try {
                val jsonArray = JSONArray(text)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    perfumes.add(Perfume(
                        brand = obj.optString("brand", "Unbekannt"),
                        name = obj.optString("name", "Unbekannter Duft"),
                        bottleSize = obj.optInt("bottleSize", 100),
                        remainingMl = obj.optDouble("remainingMl", 100.0),
                        price = obj.optDouble("price", 0.0),
                        rating = obj.optDouble("rating", 7.0),
                        type = obj.optString("type", "Unbekannt"),
                        concentration = obj.optString("concentration", "EDP"),
                        season = obj.optString("season", "Alle"),
                        occasion = obj.optString("occasion", "Alle"),
                        notes = obj.optString("notes", ""),
                        isFavorite = obj.optBoolean("isFavorite", false),
                        purchaseDate = obj.optString("purchaseDate", ""),
                        addedDate = obj.optString("addedDate", LocalDate.now().toString()),
                        imageUrl = obj.optString("imageUrl", ""),
                        isWishlist = obj.optBoolean("isWishlist", false)
                    ))
                }
            } catch (_: Exception) {
                text.lines().forEach { line ->
                    val parts = line.split(";")
                    if (parts.size >= 2) {
                        perfumes.add(Perfume(
                            brand = parts[0].trim(),
                            name = parts[1].trim(),
                            bottleSize = parts.getOrNull(2)?.toIntOrNull() ?: 100,
                            remainingMl = parts.getOrNull(3)?.replace(",", ".")?.toDoubleOrNull() ?: 100.0,
                            price = parts.getOrNull(4)?.replace(",", ".")?.toDoubleOrNull() ?: 0.0,
                            rating = 7.0,
                            type = "Unbekannt",
                            addedDate = LocalDate.now().toString()
                        ))
                    }
                }
            }
            if (perfumes.isNotEmpty()) repo.addPerfumes(perfumes)
        }
    }

    fun addLog(perfumeId: Int, occasion: String, weather: String, note: String, sprays: Int) {
        viewModelScope.launch {
            repo.getPerfumeById(perfumeId).firstOrNull()?.let { perfume ->
                val reduction = sprays.toDouble() / 15.0
                val newRemaining = (perfume.remainingMl - reduction).coerceIn(0.0, perfume.bottleSize.toDouble())
                val roundedRemaining = round(newRemaining * 10000.0) / 10000.0
                repo.addLog(UsageLog(
                    perfumeId = perfumeId,
                    date = LocalDate.now().toString(),
                    occasion = occasion,
                    weather = weather,
                    note = note,
                    sprays = sprays
                ))
                repo.updateRemainingMl(perfumeId, roundedRemaining)
            }
        }
    }

    fun deleteLog(log: UsageLog) {
        viewModelScope.launch {
            repo.getPerfumeById(log.perfumeId).firstOrNull()?.let { perfume ->
                val addition = log.sprays.toDouble() / 15.0
                val newRemaining = (perfume.remainingMl + addition).coerceIn(0.0, perfume.bottleSize.toDouble())
                val roundedRemaining = round(newRemaining * 10000.0) / 10000.0
                repo.updateRemainingMl(perfume.id, roundedRemaining)
            }
            repo.deleteLog(log)
        }
    }

    fun updateLog(log: UsageLog, oldSprays: Int) {
        viewModelScope.launch {
            repo.updateLog(log)
            val diff = (log.sprays - oldSprays).toDouble() / 15.0
            repo.getPerfumeById(log.perfumeId).firstOrNull()?.let { perfume ->
                val newRemaining = (perfume.remainingMl - diff).coerceIn(0.0, perfume.bottleSize.toDouble())
                val roundedRemaining = round(newRemaining * 10000.0) / 10000.0
                repo.updateRemainingMl(perfume.id, roundedRemaining)
            }
        }
    }

    fun getPerfumeById(id: Int) = repo.getPerfumeById(id)
    fun getLogsForPerfume(id: Int) = repo.getLogsForPerfume(id)
    fun getUsageCount(id: Int) = repo.getUsageCount(id)
}

enum class SortMode {
    BRAND, RATING, NAME, RECENT;
    fun apply(list: List<Perfume>): List<Perfume> = when (this) {
        BRAND -> list.sortedWith(compareBy({ it.brand }, { it.name }))
        RATING -> list.sortedByDescending { it.rating }
        NAME -> list.sortedBy { it.name }
        RECENT -> list.sortedByDescending { it.addedDate }
    }
}

class PerfumeViewModelFactory(
    private val application: Application,
    private val repo: PerfumeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfumeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PerfumeViewModel(application, repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
