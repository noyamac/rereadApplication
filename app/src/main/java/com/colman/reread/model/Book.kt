package com.colman.reread.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val price: Double,
    val description: String,
    val summary: String,
    val imageUrl: String,
    val contactPhone: String,
    val sellerName: String,
    val sellerEmail: String
) : Parcelable
