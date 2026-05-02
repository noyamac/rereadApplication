package com.colman.reread.data.repository

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import com.colman.reread.base.ErrorCompletion
import com.colman.reread.base.SuccessCompletion
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.data.models.StorageModel
import com.colman.reread.model.User

class AuthRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()
    private val storageModel = StorageModel()

    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = AuthRepository()
    }

    fun signUp(
        user: User,
        password: String,
        profileImage: Bitmap?,
        onSuccess: SuccessCompletion,
        onError: ErrorCompletion
    ) {
        firebaseAuth.signUp(
            email = user.email,
            password = password,
            onSuccess = { uid ->
                fun saveUser(userToSave: User) {
                    firebaseModel.addUser(
                        user = userToSave,
                        onSuccess = {
                            mainHandler.post { onSuccess() }
                        },
                        onError = {
                            mainHandler.post {
                                firebaseAuth.deleteCurrentUser()
                                onError("Sign up failed while saving profile")
                            }
                        }
                    )
                }

                if (profileImage != null) {
                    storageModel.uploadImage(
                        folderPath = "users/$uid",
                        image = profileImage,
                        completion = { imageUrl ->
                            if (imageUrl == null) {
                                mainHandler.post {
                                    firebaseAuth.deleteCurrentUser()
                                    onError("Failed to upload profile image")
                                }
                                return@uploadImage
                            }
                            val userWithId = user.copy(id = uid, profileImageUrl = imageUrl)
                            saveUser(userWithId)
                        }
                    )
                } else {
                    val userWithId = user.copy(id = uid)
                    saveUser(userWithId)
                }
            },
            onError = { error ->
                mainHandler.post { onError(error) }
            }
        )
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: SuccessCompletion,
        onError: ErrorCompletion
    ) {
        firebaseAuth.signIn(
            email = email,
            password = password,
            onSuccess = { uid ->
                firebaseModel.getUserById(
                    id = uid,
                    onSuccess = { user ->
                        mainHandler.post {
                            if (user != null) {
                                onSuccess()
                            } else {
                                firebaseAuth.signOut()
                                onError("User is not registered in database")
                            }
                        }
                    },
                    onError = {
                        mainHandler.post { onError("Failed to verify user profile. Please try again") }
                    }
                )
            },
            onError = { error ->
                mainHandler.post { onError(error) }
            }
        )
    }
}
