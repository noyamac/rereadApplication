package com.colman.reread.data.models

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth

class FirebaseAuthModel {

    private var auth: FirebaseAuth = Firebase.auth

    fun signUp(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.uid?.let(onSuccess) ?: onError("User data not found after sign up")
                } else {
                    onError(mapAuthError(task.exception))
                }
            }
    }

    fun signIn(email: String, password: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.uid?.let(onSuccess) ?: onError("User data not found after sign in")
                } else {
                    onError(mapAuthError(task.exception))
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
