package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import com.colman.reread.base.Completion
import com.colman.reread.base.UserCompletion
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.User
import java.util.concurrent.Executors

class UsersRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = UsersRepository()
    }

    fun signUp(user: User, password: String, completion: Completion) {
        firebaseAuth.signUp(user.email, password) { uid ->
            if (uid != null) {
                val userWithId = user.copy(id = uid)
                firebaseModel.addUser(userWithId) {
                    mainHandler.post { completion() }
                }
            } else {
                mainHandler.post { completion() }
            }
        }
    }

    fun signIn(email: String, password: String, completion: (String?) -> Unit) {
        firebaseAuth.signIn(email, password) { uid ->
            mainHandler.post { completion(uid) }
        }
    }

    fun getCurrentUser(completion: UserCompletion) {
        val uid = firebaseAuth.getUserId()
        if (uid != null) {
            firebaseModel.getUserById(uid) { user ->
                mainHandler.post { completion(user) }
            }
        } else {
            completion(null)
        }
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.isUserLoggedIn()

    fun signOut() = firebaseAuth.signOut()
}
