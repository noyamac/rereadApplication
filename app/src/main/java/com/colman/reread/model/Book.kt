package com.colman.reread.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val price: Double,
    val description: String,
    val summary: String,
    val imageUrl: String,
    val contactPhone: String
)
