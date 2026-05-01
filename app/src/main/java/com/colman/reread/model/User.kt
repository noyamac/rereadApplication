package com.colman.reread.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val country: String = "",
    val city: String = "",
    val profileImageUrl: String = ""
) : Parcelable
