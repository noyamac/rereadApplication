package com.colman.reread.features.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.User
import com.colman.reread.model.UserRepository

class ProfileViewModel : ViewModel() {

    val userData: LiveData<User> = UserRepository.userLiveData
}
