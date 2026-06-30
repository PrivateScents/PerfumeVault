package com.perfumevault.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.*
import com.perfumevault.data.CatalogDefaults
import com.perfumevault.data.CatalogPerfume
import com.perfumevault.data.Perfume
import com.perfumevault.data.UsageLog
import com.perfumevault.repository.PerfumeRepository
import com.perfumevault.util.BillingManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.time.LocalDate
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class PerfumeViewModel(
    application: Application,
    private val repo: PerfumeRepository
) : AndroidViewModel(application) {

    private val billingManager = BillingManager(application)
    val isAdFree = billingManager.isAdFree

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

    private val _showTour = MutableStateFlow(prefs.getBoolean("show_tour_v2", true))
    val showTour: StateFlow<Boolean> = _showTour.asStateFlow()

    private val _tourStep = MutableStateFlow(-1)
    val tourStep: StateFlow<Int> = _tourStep.asStateFlow()

    private val _sortMode = MutableStateFlow(
        runCatching { SortMode.valueOf(prefs.getString("sort_mode", "BRAND") ?: "BRAND") }.getOrDefault(SortMode.BRAND)
    )
    val sortMode: StateFlow<SortMode> = _sortMode.asStateFlow()

    private val _filterFavorites = MutableStateFlow(value = false)
    val filterFavorites: StateFlow<Boolean> = _filterFavorites.asStateFlow()

    private val _filterSamples = MutableStateFlow(value = false)
    val filterSamples: StateFlow<Boolean> = _filterSamples.asStateFlow()

    private val _filterSeason = MutableStateFlow(prefs.getString("filter_season", "Alle") ?: "Alle")
    val filterSeason: StateFlow<String> = _filterSeason.asStateFlow()

    private val _notificationHour = MutableStateFlow(prefs.getInt("notification_hour", 7))
    val notificationHour: StateFlow<Int> = _notificationHour.asStateFlow()

    private val _notificationMinute = MutableStateFlow(prefs.getInt("notification_minute", 0))
    val notificationMinute: StateFlow<Int> = _notificationMinute.asStateFlow()

    private val _rescheduleTrigger = MutableSharedFlow<Unit>()
    val rescheduleTrigger = _rescheduleTrigger.asSharedFlow()

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
        _searchQuery.flatMapLatest { query ->
            val q = query.trim()
            if (q.isBlank()) repo.allPerfumes 
            else repo.searchPerfumes(q)
        },
        _sortMode,
        _filterFavorites,
        _filterSamples,
        _filterSeason
    ) { list, sort, favOnly, samplesOnly, season ->
        var filtered = list
        
        // Filter by Sample status: If samplesOnly is true, show only samples. 
        // If false, show only full bottles.
        filtered = filtered.filter { it.isSample == samplesOnly }

        if (favOnly) {
            filtered = filtered.filter { it.isFavorite }
        }
        
        if (season != "Alle") {
            filtered = filtered.filter { it.season.contains(season) || (it.season == "Alle") }
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

    val absoluteAllPerfumes: StateFlow<List<Perfume>> = repo.absoluteAllPerfumes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPerfumeSizes: StateFlow<List<com.perfumevault.data.PerfumeSize>> = repo.allSizes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistPerfumes: StateFlow<List<Perfume>> = combine(
        repo.wishlistPerfumes,
        _searchQuery,
        _filterSamples,
        _filterSeason
    ) { list, query, samplesOnly, season ->
        val q = query.trim()
        var filtered = list
        
        // Filter by Sample status
        filtered = filtered.filter { it.isSample == samplesOnly }

        if (q.isNotBlank()) {
            filtered = filtered.filter { 
                it.name.contains(q, true) || 
                it.brand.contains(q, true) || 
                it.notes.contains(q, true) ||
                "${it.brand} ${it.name}".contains(q, true) ||
                "${it.name} ${it.brand}".contains(q, true)
            }
        }

        if (season != "Alle") {
            filtered = filtered.filter { it.season.contains(season) || (it.season == "Alle") }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Catalog ---
    private val _catalogQuery = MutableStateFlow("")
    val catalogQuery: StateFlow<String> = _catalogQuery.asStateFlow()

    val catalogResults: StateFlow<List<CatalogPerfume>> = _catalogQuery
        .debounce(200.milliseconds)
        .flatMapLatest { if (it.isBlank()) flowOf(emptyList()) else repo.searchCatalog(it.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Force-populate and update catalog with latest defaults
        viewModelScope.launch {
            val defaults = CatalogDefaults.get()
            repo.insertCatalog(defaults)
        }
    }

    // --- Actions ---
    fun setCatalogQuery(q: String) { _catalogQuery.value = q }
    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setTab(tab: Int) { _selectedTab.value = tab }

    fun dismissTour() {
        _showTour.value = false
        prefs.edit { putBoolean("show_tour_v2", false) }
        setTab(0) // Zurück zur Startseite (Sammlung)
    }

    fun nextTourStep() {
        _tourStep.value += 1
    }

    fun setSortMode(mode: SortMode) { 
        _sortMode.value = mode 
        prefs.edit { putString("sort_mode", mode.name) }
    }
    fun toggleFavoriteFilter() { _filterFavorites.value = !_filterFavorites.value }
    fun toggleSampleFilter() { _filterSamples.value = !_filterSamples.value }

    fun setFilterSeason(season: String) {
        _filterSeason.value = season
        prefs.edit { putString("filter_season", season) }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        _notificationHour.value = hour
        _notificationMinute.value = minute
        prefs.edit { 
            putInt("notification_hour", hour)
            putInt("notification_minute", minute)
        }
        viewModelScope.launch {
            _rescheduleTrigger.emit(Unit)
        }
    }

    fun addPerfume(
        name: String, brand: String, rating: Double, type: String,
        concentration: String, season: String, occasion: String,
        bottleSize: Int, remainingMl: Double, price: Double, notes: String, imageUrl: String,
        isWishlist: Boolean = false,
        allAvailableSizes: String = "", // Komma-separierte Liste, z.B. "50,100"
        isSample: Boolean = false
    ) {
        viewModelScope.launch {
            val perfumeId = java.util.UUID.randomUUID().toString()
            val perfume = Perfume(
                id = perfumeId,
                name = name, brand = brand, rating = rating, type = type,
                concentration = concentration, season = season, occasion = occasion,
                bottleSize = bottleSize,
                remainingMl = remainingMl,
                price = price,
                notes = notes,
                imageUrl = imageUrl,
                addedDate = LocalDate.now().toString(),
                isWishlist = isWishlist,
                isSample = isSample
            )
            repo.addPerfume(perfume)
            
            // In die Sizes-Tabelle eintragen
            val sizeList = if (allAvailableSizes.isNotEmpty()) {
                allAvailableSizes.split(",").mapNotNull { it.trim().toIntOrNull() }.distinct()
            } else {
                listOf(bottleSize)
            }
            
            val perfumeSizes = sizeList.map { 
                com.perfumevault.data.PerfumeSize(perfumeId = perfumeId, ml = it) 
            }
            repo.addSizes(perfumeSizes)
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

    fun updateRemainingMl(id: String, ml: Double) {
        viewModelScope.launch { repo.updateRemainingMl(id, ml) }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repo.deleteAllLogs()
            repo.deleteAllPerfumes()
            repo.deleteAllSizes()
        }
    }

    private val _importError = MutableStateFlow<String?>(null)
    val importError: StateFlow<String?> = _importError.asStateFlow()

    private val _importSuccessMessage = MutableStateFlow<String?>(null)
    val importSuccessMessage: StateFlow<String?> = _importSuccessMessage.asStateFlow()

    private val _importSuccess = MutableSharedFlow<Unit>()
    val importSuccess = _importSuccess.asSharedFlow()

    fun clearImportStatus() { 
        _importError.value = null 
        _importSuccessMessage.value = null
    }

    fun addPerfumesFromText(text: String) {
        viewModelScope.launch {
            _importError.value = null
            _importSuccessMessage.value = null
            
            val newPerfumes = mutableListOf<Perfume>()
            val newLogs = mutableListOf<UsageLog>()
            val newSizes = mutableListOf<com.perfumevault.data.PerfumeSize>()
            
            try {
                val trimmed = text.trim()
                if (trimmed.isEmpty()) {
                    _importError.value = t("Text ist leer", "Text is empty")
                    return@launch
                }

                // Aktuelle Sammlung laden für Duplikats-Check und Update-ID Mapping
                val existing = repo.allPerfumes.first() + repo.wishlistPerfumes.first()
                val existingMap = existing.associateBy { "${it.brand.lowercase().trim()}|${it.name.lowercase().trim()}" }

                var updatedCount = 0
                var addedCount = 0

                if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                    // --- JSON Format ---
                    val rootPerfumes = mutableListOf<Perfume>()
                    if (trimmed.startsWith("[")) {
                        val jsonArray = JSONArray(trimmed)
                        for (i in 0 until jsonArray.length()) {
                            rootPerfumes.add(parsePerfumeJson(jsonArray.getJSONObject(i)))
                        }
                    } else {
                        val root = org.json.JSONObject(trimmed)
                        val perfumesArray = root.optJSONArray("perfumes") ?: root.optJSONArray("parfueme")
                        if (perfumesArray != null) {
                            for (i in 0 until perfumesArray.length()) {
                                rootPerfumes.add(parsePerfumeJson(perfumesArray.getJSONObject(i)))
                            }
                        }
                        
                        // Logs und Sizes
                        val logsArray = root.optJSONArray("logs") ?: root.optJSONArray("eintraege")
                        if (logsArray != null) {
                            for (i in 0 until logsArray.length()) {
                                val obj = logsArray.getJSONObject(i)
                                newLogs.add(UsageLog(
                                    perfumeId = obj.optString("perfumeId", obj.optString("parfuemId", "")),
                                    date = obj.optString("date", obj.optString("datum", "")),
                                    occasion = obj.optString("occasion", obj.optString("anlass", "")),
                                    weather = obj.optString("weather", obj.optString("wetter", "")),
                                    note = obj.optString("note", obj.optString("notiz", "")),
                                    sprays = obj.optInt("sprays", obj.optInt("sprueher", 1))
                                ))
                            }
                        }
                    }

                    rootPerfumes.forEach { p ->
                        val key = "${p.brand.lowercase().trim()}|${p.name.lowercase().trim()}"
                        val existingPerfume = existingMap[key]
                        if (existingPerfume != null) {
                            // Update: Behalte die existierende ID
                            newPerfumes.add(p.copy(id = existingPerfume.id))
                            updatedCount++
                        } else {
                            // Neu hinzufügen
                            newPerfumes.add(p)
                            addedCount++
                        }
                    }
                } else {
                    // --- CSV / Text Format ---
                    trimmed.lines().forEachIndexed { index, line ->
                        if (line.isBlank()) return@forEachIndexed
                        val parts = line.split(";")
                        if (parts.size >= 2) {
                            val brand = parts[0].trim()
                            val name = parts[1].trim()
                            val key = "${brand.lowercase()}|${name.lowercase()}"
                            
                            val existingPerfume = existingMap[key]
                            val perfumeToSave = Perfume(
                                id = existingPerfume?.id ?: java.util.UUID.randomUUID().toString(),
                                brand = brand,
                                name = name,
                                bottleSize = parts.getOrNull(2)?.toIntOrNull() ?: (existingPerfume?.bottleSize ?: 100),
                                remainingMl = parts.getOrNull(3)?.replace(",", ".")?.toDoubleOrNull() ?: (existingPerfume?.remainingMl ?: 100.0),
                                price = parts.getOrNull(4)?.replace(",", ".")?.toDoubleOrNull() ?: (existingPerfume?.price ?: 0.0),
                                isSample = parts.getOrNull(5)?.trim()?.lowercase() == "true" || (existingPerfume?.isSample ?: false),
                                rating = parts.getOrNull(6)?.replace(",", ".")?.toDoubleOrNull()?.coerceIn(1.0, 10.0) ?: (existingPerfume?.rating ?: 7.0),
                                concentration = parts.getOrNull(7)?.trim() ?: (existingPerfume?.concentration ?: "EDP"),
                                season = parts.getOrNull(8)?.trim() ?: (existingPerfume?.season ?: "Alle"),
                                occasion = parts.getOrNull(9)?.trim() ?: (existingPerfume?.occasion ?: "Alle"),
                                notes = parts.getOrNull(10)?.trim() ?: (existingPerfume?.notes ?: ""),
                                imageUrl = parts.getOrNull(11)?.trim() ?: (existingPerfume?.imageUrl ?: ""),
                                isWishlist = parts.getOrNull(12)?.trim()?.lowercase() == "true" || (existingPerfume?.isWishlist ?: false),
                                addedDate = existingPerfume?.addedDate ?: LocalDate.now().toString()
                            )
                            
                            newPerfumes.add(perfumeToSave)
                            if (existingPerfume != null) updatedCount++ else addedCount++
                        } else {
                            if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                                throw Exception(t("Zeile ${index + 1} ungültig", "Line ${index + 1} invalid"))
                            }
                        }
                    }
                }
                
                if (newPerfumes.isNotEmpty()) repo.addPerfumes(newPerfumes)
                if (newLogs.isNotEmpty()) repo.addLogs(newLogs)
                if (newSizes.isNotEmpty()) repo.addSizes(newSizes)

                val msg = if (addedCount > 0 && updatedCount > 0) {
                    t("Erfolg: $addedCount neu, $updatedCount aktualisiert.", "Success: $addedCount new, $updatedCount updated.")
                } else if (addedCount > 0) {
                    t("Erfolg: $addedCount Düfte hinzugefügt.", "Success: $addedCount fragrances added.")
                } else if (updatedCount > 0) {
                    t("Erfolg: $updatedCount Düfte aktualisiert.", "Success: $updatedCount fragrances updated.")
                } else {
                    t("Keine Änderungen vorgenommen.", "No changes made.")
                }
                
                _importSuccessMessage.value = msg
                kotlinx.coroutines.delay(2000)
                _importSuccess.emit(Unit)

            } catch (e: Exception) {
                _importError.value = t("Fehler: ", "Error: ") + (e.message ?: t("Format ungültig", "Invalid format"))
            }
        }
    }

    private fun parsePerfumeJson(obj: org.json.JSONObject): Perfume {
        return Perfume(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            brand = obj.optString("brand", obj.optString("marke", "Unbekannt")),
            name = obj.optString("name", "Unbekannter Duft"),
            bottleSize = obj.optInt("bottleSize", obj.optInt("groesse", obj.optInt("flaschenGroesse", 100))),
            remainingMl = obj.optDouble("remainingMl", obj.optDouble("fuellstand", 100.0)),
            rating = obj.optDouble("rating", obj.optDouble("bewertung", 7.0)),
            type = obj.optString("type", obj.optString("typ", "Unbekannt")),
            concentration = obj.optString("concentration", obj.optString("konzentration", "EDP")),
            season = obj.optString("season", obj.optString("jahreszeit", "Alle")),
            occasion = obj.optString("occasion", obj.optString("anlass", "Alle")),
            notes = obj.optString("notes", obj.optString("notizen", "")),
            isFavorite = obj.optBoolean("isFavorite", obj.optBoolean("is_favourite", obj.optBoolean("istFavorit", false))),
            purchaseDate = obj.optString("purchaseDate", obj.optString("purchase_date", obj.optString("kaufdatum", ""))),
            addedDate = obj.optString("addedDate", obj.optString("added_date", obj.optString("hinzugefuegtAm", LocalDate.now().toString()))),
            imageUrl = obj.optString("imageUrl", obj.optString("bildUrl", "")),
            price = obj.optDouble("price", obj.optDouble("preis", 0.0)),
            isWishlist = obj.optBoolean("isWishlist", obj.optBoolean("is_wishlist", obj.optBoolean("aufMerkliste", false))),
            isSample = obj.optBoolean("isSample", obj.optBoolean("istProbe", false))
        )
    }

    fun addLog(perfumeId: String, occasion: String, weather: String, note: String, sprays: Int) {
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

    fun getPerfumeById(id: String) = repo.getPerfumeById(id)
    fun getLogsForPerfume(id: String) = repo.getLogsForPerfume(id)
    fun getUsageCount(id: String) = repo.getUsageCount(id)
    fun getSizesForPerfume(id: String) = repo.getSizesForPerfume(id)

    fun purchaseRemoveAds(activity: android.app.Activity) {
        billingManager.purchaseRemoveAds(activity)
    }
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
