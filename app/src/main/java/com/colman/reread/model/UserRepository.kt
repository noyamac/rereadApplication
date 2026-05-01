package com.colman.reread.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserRepository {
    private val _userLiveData = MutableLiveData<User>(User(
        id = "1",
        name = "John Doe",
        email = "john.doe@example.com",
        phone = "+1 234 567 890",
        country = "United States",
        city = "New York",
        profileImageUrl = ""
    ))
    
    val userLiveData: LiveData<User> = _userLiveData

    val currentUser: User
        get() = _userLiveData.value!!

    fun updateUser(name: String, phone: String, country: String, city: String, profileImageUrl: String) {
        val updatedUser = currentUser.copy(
            name = name,
            phone = phone,
            country = country,
            city = city,
            profileImageUrl = profileImageUrl
        )
        _userLiveData.value = updatedUser
    }
}
