package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.User
import com.colman.reread.model.UserRepository

class EditProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    init {
        _user.value = UserRepository.currentUser
    }

    fun saveProfile(name: String, phone: String, country: String, city: String, imageUrl: String) {
        UserRepository.updateUser(name, phone, country, city, imageUrl)
        _updateStatus.value = true
    }
}
