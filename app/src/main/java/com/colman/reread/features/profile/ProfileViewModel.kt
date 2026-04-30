package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.User

class ProfileViewModel : ViewModel() {

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        loadMockUser()
    }

    private fun loadMockUser() {
        _userData.value = User(
            id = "1",
            name = "John Doe",
            phone = "+1 234 567 890",
            profileImageUrl = ""
        )
    }
}
