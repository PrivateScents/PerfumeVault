package com.perfumevault.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_logs",
    foreignKeys = [ForeignKey(
        entity = Perfume::class,
        parentColumns = ["id"],
        childColumns = ["perfumeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["perfumeId"])]
)
data class UsageLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val perfumeId: String,
    val date: String,       // YYYY-MM-DD
    val occasion: String = "",
    val weather: String = "",
    val note: String = "",
    val sprays: Int = 3
)
