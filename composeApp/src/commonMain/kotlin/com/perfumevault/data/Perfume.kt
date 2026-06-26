package com.perfumevault.data

data class Perfume(
    val id: String = "",
    val brand: String = "",
    val name: String = "",
    val addedDate: String = "",
    val purchaseDate: String = "",
    val isFavorite: Boolean = false,
    val rating: Double = 0.0,
    val isWishlist: Boolean = false,
    val type: String = "",
    val concentration: String = "EDP",
    val season: String = "Alle",
    val occasion: String = "Alle",
    val notes: String = "",
    val bottleSize: Int = 100,
    val remainingMl: Double = 100.0,
    val price: Double = 0.0,
    val imageUrl: String = ""
)
