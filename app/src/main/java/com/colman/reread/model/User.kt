package com.colman.reread.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "User")
@Parcelize
data class User(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val country: String = "",
    val city: String = "",
    val profileImageUrl: String = ""
) : Parcelable
