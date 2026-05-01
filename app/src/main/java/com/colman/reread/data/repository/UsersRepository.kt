package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import com.colman.reread.base.UserCompletion
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.User

class UsersRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()

    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    companion object {
        val shared = UsersRepository()
    }

    fun signUp(user: User, password: String, completion: (Boolean, String?) -> Unit) {
        firebaseAuth.signUp(user.email, password) { uid, authError ->
            if (uid != null) {
                val userWithId = user.copy(id = uid)
                firebaseModel.addUser(userWithId) { saveSuccess ->
                    mainHandler.post {
                        if (saveSuccess) {
                            completion(true, null)
                        } else {
                            firebaseAuth.deleteCurrentUser()
                            completion(false, "Sign up failed while saving profile")
                        }
                    }
                }
            } else {
                mainHandler.post { completion(false, authError ?: "Sign up failed") }
            }
        }
    }

    fun signIn(email: String, password: String, completion: (Boolean, String?) -> Unit) {
        firebaseAuth.signIn(email, password) { uid, authError ->
            if (uid == null) {
                mainHandler.post { completion(false, authError ?: "Invalid email or password") }
                return@signIn
            }

            firebaseModel.getUserById(uid) { user ->
                mainHandler.post {
                    if (user != null) {
                        completion(true, null)
                    } else {
                        firebaseAuth.signOut()
                        completion(false, "User is not registered in database")
                    }
                }
            }
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
