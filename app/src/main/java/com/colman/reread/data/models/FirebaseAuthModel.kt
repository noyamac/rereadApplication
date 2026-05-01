package com.colman.reread.data.models

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class FirebaseAuthModel {

    private var auth: FirebaseAuth = Firebase.auth

    fun signUp(email: String, password: String, completion: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(auth.currentUser?.uid)
                } else {
                    Log.w("TAG", "signUp: failure", task.exception)
                    completion(null)
                }
            }
    }

    fun signIn(email: String, password: String, completion: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completion(auth.currentUser?.uid)
                } else {
                    Log.w("TAG", "signIn: failure", task.exception)
                    completion(null)
                }
            }
    }

    fun getUserId(): String? = auth.currentUser?.uid
    
    fun isUserLoggedIn(): Boolean = auth.currentUser != null
    
    fun signOut() = auth.signOut()
}
