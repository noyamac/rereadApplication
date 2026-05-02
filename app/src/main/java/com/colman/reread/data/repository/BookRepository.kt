package com.colman.reread.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.Book

object BookRepository {
    private const val tag = "BookRepository"

    private val firebaseModel = FirebaseModel()

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    init {
        firebaseModel.listenToBooks(
            onBooksUpdated = { bookList ->
                _books.postValue(bookList)
            },
            onError = { error ->
                Log.e(tag, "Error fetching books snapshot: $error")
            }
        )
    }

    fun addBook(book: Book, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.addBook(
            book = book,
            onSuccess = { onComplete?.invoke(true) },
            onError = { e ->
                Log.e(tag, "Error adding book: $e")
                onComplete?.invoke(false)
            }
        )
    }

    fun updateBook(updatedBook: Book, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.updateBook(
            book = updatedBook,
            onSuccess = { onComplete?.invoke(true) },
            onError = { e ->
                Log.e(tag, "Error updating book: $e")
                onComplete?.invoke(false)
            }
        )
    }

    fun deleteBook(bookId: String, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.deleteBook(
            bookId = bookId,
            onSuccess = { onComplete?.invoke(true) },
            onError = { e ->
                Log.e(tag, "Error deleting book: $e")
                onComplete?.invoke(false)
            }
        )
    }
}