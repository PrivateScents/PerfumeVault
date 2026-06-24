package com.perfumevault.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "perfume_catalog",
    indices = [Index(value = ["brand", "name"], unique = true)]
)
data class CatalogPerfume(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val brand: String,
    val concentration: String,
    val imageUrl: String = "",
    val description: String = "",
    val releaseYear: Int = 0,
    val defaultSeason: String = "Alle",
    val defaultOccasion: String = "Alle",
    val availableSizes: String = "30,50,100"
)
