package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.data.repository.UserRepository
import com.colman.reread.model.User

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository.shared

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    private val _authRequired = MutableLiveData<Boolean>()
    val authRequired: LiveData<Boolean> = _authRequired

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        repository.getCurrentUser { user ->
            if (user != null) {
                _userData.value = user
                _authRequired.value = false
            } else {
                _authRequired.value = true
            }
        }
    }
}
