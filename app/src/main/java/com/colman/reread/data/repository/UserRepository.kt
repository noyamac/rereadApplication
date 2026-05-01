package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import com.colman.reread.base.UserCompletion
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel

class UserRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()

    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = UserRepository()
    }

    fun getCurrentUser(completion: UserCompletion) {
        val uid = firebaseAuth.getUserId()
        if (uid != null) {
            firebaseModel.getUserById(
                id = uid,
                onSuccess = { user ->
                    mainHandler.post { completion(user) }
                },
                onError = {
                    mainHandler.post { completion(null) }
                }
            )
        } else {
            mainHandler.post { completion(null) }
        }
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.isUserLoggedIn()

    fun signOut() = firebaseAuth.signOut()
}
