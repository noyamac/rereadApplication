package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import com.colman.reread.base.ErrorCompletion
import com.colman.reread.base.SuccessCompletion
import com.colman.reread.base.UserCompletion
import com.colman.reread.dao.AppLocalDb
import com.colman.reread.dao.AppLocalDbRepository
import com.colman.reread.data.models.FirebaseAuthModel
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.Book
import com.colman.reread.model.User

class UserRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()
    private val database: AppLocalDbRepository = AppLocalDb.db
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    var currentUser: User? = null
        private set

    companion object {
        val shared = UserRepository()
    }

    fun addUser(user: User) = {
        database.userDao.insertUsers(user)
    }
    fun deleteUser(user: User) = {
        database.userDao.delete(user)
    }
    fun getUserById(userId: String) = database.userDao.getUserById(userId)

    fun getCurrentUser(completion: UserCompletion) {
        val uid = firebaseAuth.getUserId()
        if (uid != null) {
            firebaseModel.getUserById(
                id = uid,
                onSuccess = { user ->
                    currentUser = user
                    mainHandler.post { completion(user) }
                },
                onError = {
                    currentUser = null
                    mainHandler.post { completion(null) }
                }
            )
        } else {
            currentUser = null
            mainHandler.post { completion(null) }
        }
    }

    fun isUserLoggedIn(): Boolean = firebaseAuth.isUserLoggedIn()

    fun signOut() {
        currentUser = null
        firebaseAuth.signOut()
    }

    fun updateUser(user: User, onSuccess: SuccessCompletion, onError: ErrorCompletion) {
        firebaseModel.addUser(
            user = user,
            onSuccess = {
                currentUser = user
                firebaseModel.updateSellerNameInBooks(user.email, user.name)
                mainHandler.post { onSuccess() }
            },
            onError = { err -> mainHandler.post { onError(err) } }
        )
    }
}
