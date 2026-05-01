package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import com.colman.reread.base.ErrorCompletion
import com.colman.reread.base.SuccessCompletion
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.User

class AuthRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()

    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = AuthRepository()
    }

    fun signUp(
        user: User,
        password: String,
        onSuccess: SuccessCompletion,
        onError: ErrorCompletion
    ) {
        firebaseAuth.signUp(
            email = user.email,
            password = password,
            onSuccess = { uid ->
                val userWithId = user.copy(id = uid)
                firebaseModel.addUser(
                    user = userWithId,
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
