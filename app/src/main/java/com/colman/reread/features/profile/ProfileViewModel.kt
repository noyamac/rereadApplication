package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.data.repository.UsersRepository
import com.colman.reread.model.User

class ProfileViewModel : ViewModel() {

    private val repository = UsersRepository.shared

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        repository.getCurrentUser { user ->
            _userData.value = user ?: User()
        }
    }
}
