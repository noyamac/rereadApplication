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
import com.colman.reread.model.User
import java.util.concurrent.Executors

class UserRepository private constructor() {

    private val firebaseModel = FirebaseModel()
    private val firebaseAuth = FirebaseAuthModel()
    private val database: AppLocalDbRepository = AppLocalDb.db
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    var currentUser: User? = null
        private set

    companion object {
        val shared = UserRepository()
    }

    fun addUser(user: User, onComplete: ((Boolean) -> Unit)? = null) {
        executor.execute {
            try {
                database.userDao.insertUsers(user)
                mainHandler.post { onComplete?.invoke(true) }
            } catch (e: Exception) {
                mainHandler.post { onComplete?.invoke(false) }
            }
        }
    }
    
    fun deleteUser(user: User, onComplete: ((Boolean) -> Unit)? = null) {
        executor.execute {
            try {
                database.userDao.delete(user)
                mainHandler.post { onComplete?.invoke(true) }
            } catch (e: Exception) {
                mainHandler.post { onComplete?.invoke(false) }
            }
        }
    }
    fun getUserById(userId: String) = database.userDao.getUserById(userId)

    fun getCurrentUser(completion: UserCompletion) {
        val uid = firebaseAuth.getUserId()
        if (uid != null) {
            firebaseModel.getUserById(
                id = uid,
                onSuccess = { user ->
                    executor.execute {
                        if (user != null) {
                            database.userDao.insertUsers(user)
                        }
                        mainHandler.post { 
                            currentUser = user
                            completion(user) 
                        }
                    }
                },
                onError = {
                    executor.execute {
                        val localUser = database.userDao.getUserByIdSync(uid)
                        mainHandler.post {
                            currentUser = localUser
                            completion(localUser)
                        }
                    }
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
                executor.execute {
                    database.userDao.insertUsers(user)
                    mainHandler.post {
                        currentUser = user
                        firebaseModel.updateSellerNameInBooks(user.email, user.name)
                        onSuccess()
                    }
                }
            },
            onError = { err -> mainHandler.post { onError(err) } }
        )
    }
}
