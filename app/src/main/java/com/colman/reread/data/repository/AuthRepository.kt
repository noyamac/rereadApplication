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
        firebaseAuth.signUp(user.email, password) { uid, authError ->
            if (uid != null) {
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
            } else {
                mainHandler.post { onError(authError ?: "Sign up failed") }
            }
        }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: SuccessCompletion,
        onError: ErrorCompletion
    ) {
        firebaseAuth.signIn(email, password) { uid, authError ->
            if (uid == null) {
                mainHandler.post { onError(authError ?: "Invalid email or password") }
                return@signIn
            }

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
        }
    }
}
