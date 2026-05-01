package com.colman.reread.data.models

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class FirebaseAuthModel {

    private var auth: FirebaseAuth = Firebase.auth

    fun signUp(email: String, password: String, completion: (String?, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(auth.currentUser?.uid, null)
                } else {
                    Log.w("TAG", "signUp: failure", task.exception)
                    completion(null, mapAuthError(task.exception))
                }
            }
    }

    fun signIn(email: String, password: String, completion: (String?, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(auth.currentUser?.uid, null)
                } else {
                    Log.w("TAG", "signIn: failure", task.exception)
                    completion(null, mapAuthError(task.exception))
                }
            }
    }

    private fun mapAuthError(error: Exception?): String {
        return when (error) {
            is FirebaseAuthUserCollisionException -> "This email is already in use"
            is FirebaseAuthWeakPasswordException -> "Password is too weak"
            is FirebaseAuthInvalidCredentialsException -> "Invalid email or password"
            is FirebaseAuthInvalidUserException -> "No account found for this email"
            else -> error?.localizedMessage ?: "Authentication failed"
        }
    }

    fun getUserId(): String? = auth.currentUser?.uid

    fun deleteCurrentUser() {
        auth.currentUser?.delete()
    }
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    fun signOut() = auth.signOut()
}
