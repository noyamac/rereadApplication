package com.colman.reread.data.models

import com.colman.reread.base.ErrorCompletion
import com.colman.reread.base.SuccessCompletion
import com.colman.reread.base.UserCompletion
import com.colman.reread.model.User
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class FirebaseModel {

    private val db = Firebase.firestore

    fun addUser(user: User, onSuccess: SuccessCompletion, onError: ErrorCompletion) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to save user") }
    }

    fun getUserById(id: String, onSuccess: UserCompletion, onError: ErrorCompletion) {
        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    onSuccess(user)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Failed to load user")
            }
    }
}
