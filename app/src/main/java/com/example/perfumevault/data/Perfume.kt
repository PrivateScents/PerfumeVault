package com.example.perfumevault.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfumes")
data class Perfume(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val brand: String,
    val rating: Double,         // 1.0–10.0
    val type: String,          // z.B. "Holzig / Würzig"
    val concentration: String = "EDP",  // EDT, EDP, Parfum, EDC
    val season: String = "Alle",        // Frühling, Sommer, Herbst, Winter, Alle
    val occasion: String = "Alle",      // Alltag, Abend, Business, Alle
    val notes: String = "",             // freie Notizen
    val isFavorite: Boolean = false,
    val bottleSize: Int = 100,          // ml
    val remainingMl: Double = 100.0,    // Aktueller Füllstand in ml
    val purchaseDate: String = "",      // ISO-Format YYYY-MM-DD
    val price: Double = 0.0,
    val addedDate: String = "",          // ISO-Format
    val imageUrl: String = "",           // URL zum Bild
    val isWishlist: Boolean = false      // Merkliste
)
