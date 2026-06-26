package com.perfumevault.data

data class CatalogPerfume(
    val id: Int = 0,
    val name: String,
    val brand: String,
    val concentration: String,
    val availableSizes: String,
    val imageUrl: String = "",
    val defaultSeason: String = "Alle",
    val defaultOccasion: String = "Alle"
)
