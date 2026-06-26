package com.perfumevault.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "perfumes")
data class Perfume(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "brand")
    val brand: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "added_date")
    val addedDate: String = "",
    
    @ColumnInfo(name = "purchase_date")
    val purchaseDate: String = "",
    
    @ColumnInfo(name = "is_favourite")
    val isFavorite: Boolean = false,
    
    @ColumnInfo(name = "rating")
    val rating: Double,
    
    @ColumnInfo(name = "is_wishlist")
    val isWishlist: Boolean = false,

    // Beibehalten für UI-Kompatibilität
    val type: String = "",
    val concentration: String = "EDP",
    val season: String = "Alle",
    val occasion: String = "Alle",
    val notes: String = "",
    val bottleSize: Int = 100,
    val remainingMl: Double = 100.0,
    val price: Double = 0.0,
    val imageUrl: String = "",
    val isSample: Boolean = false
)
