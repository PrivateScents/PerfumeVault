package com.perfumevault.data

data class UsageLog(
    val id: Int = 0,
    val perfumeId: String,
    val date: String,
    val occasion: String = "",
    val weather: String = "",
    val note: String = "",
    val sprays: Int = 3
)
