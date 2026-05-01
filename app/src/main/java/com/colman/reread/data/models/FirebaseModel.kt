package com.colman.reread.data.models

import com.colman.reread.base.UserCompletion
import com.colman.reread.base.Completion
import com.colman.reread.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.Firebase

class FirebaseModel {

    private val db = Firebase.firestore

    fun addUser(user: User, completion: Completion) {
        db.collection("users")
            .document(user.id)
            .set(user)
            .addOnCompleteListener {
                completion()
            }
    }

    fun getUserById(id: String, completion: UserCompletion) {
        db.collection("users")
            .document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    completion(user)
                } else {
                    completion(null)
                }
            }
            .addOnFailureListener {
                completion(null)
            }
    }
}
