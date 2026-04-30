package com.colman.reread.model

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val price: Double,
    val description: String, // Book condition
    val summary: String,     // Plot summary
    val imageUrl: String,
    val contactPhone: String
)
