package com.perfumevault.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "perfume_sizes",
    foreignKeys = [
        ForeignKey(
            entity = Perfume::class,
            parentColumns = ["id"],
            childColumns = ["perfume_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("perfume_id")]
)
data class PerfumeSize(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),
    
    @ColumnInfo(name = "perfume_id")
    val perfumeId: String,
    
    @ColumnInfo(name = "ml")
    val ml: Int
)
