package com.colman.reread.data.models

import com.colman.reread.base.ErrorCompletion
import com.colman.reread.base.SuccessCompletion
import com.colman.reread.base.UserCompletion
import com.colman.reread.model.Book
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

    fun addBook(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("books")
            .document(book.id)
            .set(book.toJson)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to add book") }
    }

    fun updateBook(book: Book, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("books")
            .document(book.id)
            .set(book.toJson)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to update book") }
    }

    fun deleteBook(bookId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("books")
            .document(bookId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to delete book") }
    }

    fun listenToBooks(onBooksUpdated: (List<Book>) -> Unit, onError: (String) -> Unit) {
        db.collection("books")
            .orderBy(Book.LAST_UPDATED_KEY, com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.localizedMessage ?: "Error fetching books")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val bookList = snapshot.documents.mapNotNull { document ->
                        Book.fromJson(document.data ?: emptyMap())
                    }
                    onBooksUpdated(bookList)
                }
            }
    }
}
