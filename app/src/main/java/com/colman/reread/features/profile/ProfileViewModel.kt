package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.User
import com.colman.reread.model.UserRepository

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        loadUser()
    }

    private fun loadUser() {
        _userData.value = UserRepository.currentUser
    }
}
