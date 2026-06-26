package com.perfumevault.util

fun generateRandomId(): String {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..16)
        .map { chars.random() }
        .joinToString("")
}
