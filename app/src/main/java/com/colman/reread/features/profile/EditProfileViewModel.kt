package com.colman.reread.features.profile

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.data.models.StorageModel
import com.colman.reread.data.repository.UserRepository
import com.colman.reread.model.User

class EditProfileViewModel : ViewModel() {

    private val userRepository = UserRepository.shared
    private val storageModel = StorageModel()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    sealed class UpdateStatus {
        object Idle : UpdateStatus()
        object Success : UpdateStatus()
        data class Error(val message: String) : UpdateStatus()
    }

    private val _updateStatus = MutableLiveData<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: LiveData<UpdateStatus> = _updateStatus

    init {
        loadUser()
    }

    private fun loadUser() {
        userRepository.getCurrentUser { user ->
            _user.value = user ?: User()
        }
    }

    fun saveProfile(name: String, phone: String, country: String, profileImage: Bitmap?) {
        val current = _user.value ?: return

        if (profileImage != null) {
            storageModel.uploadImage(
                folderPath = "users/${current.id}",
                image = profileImage,
                completion = { imageUrl ->
                    if (imageUrl == null) {
                        mainHandler.post {
                            _updateStatus.value = UpdateStatus.Error("Failed to upload image")
                        }
                        return@uploadImage
                    }
                    val updated = current.copy(
                        name = name, phone = phone, country = country,
                        profileImageUrl = imageUrl
                    )
                    saveUser(updated)
                }
            )
        } else {
            val updated = current.copy(name = name, phone = phone, country = country)
            saveUser(updated)
        }
    }

    private fun saveUser(user: User) {
        userRepository.updateUser(
            user = user,
            onSuccess = { _updateStatus.value = UpdateStatus.Success },
            onError = { msg -> _updateStatus.value = UpdateStatus.Error(msg) }
        )
    }
}
