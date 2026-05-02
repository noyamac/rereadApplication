package com.colman.reread.data.repository

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.colman.reread.dao.AppLocalDb
import com.colman.reread.data.models.FirebaseModel
import com.colman.reread.model.Book
import java.util.concurrent.Executors

object BookRepository {
    private const val tag = "BookRepository"

    private val firebaseModel = FirebaseModel()
    private val localDb = AppLocalDb.db
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler.createAsync(Looper.getMainLooper())

    val books: LiveData<List<Book>> = localDb.bookDao.getAllBooks()

    init {
        firebaseModel.listenToBooks(
            onBooksUpdated = { bookList ->
                executor.execute {
                    localDb.bookDao.insertBooks(*bookList.toTypedArray())
                }
            },
            onError = { error ->
                Log.e(tag, "Error fetching books snapshot: $error")
            }
        )
    }

    fun addBook(book: Book, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.addBook(
            book = book,
            onSuccess = {
                executor.execute {
                    localDb.bookDao.insertBooks(book)
                    mainHandler.post { onComplete?.invoke(true) }
                }
            },
            onError = { e ->
                Log.e(tag, "Error adding book: $e")
                mainHandler.post { onComplete?.invoke(false) }
            }
        )
    }

    fun updateBook(updatedBook: Book, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.updateBook(
            book = updatedBook,
            onSuccess = {
                executor.execute {
                    localDb.bookDao.insertBooks(updatedBook)
                    mainHandler.post { onComplete?.invoke(true) }
                }
            },
            onError = { e ->
                Log.e(tag, "Error updating book: $e")
                mainHandler.post { onComplete?.invoke(false) }
            }
        )
    }

    fun deleteBook(bookId: String, onComplete: ((Boolean) -> Unit)? = null) {
        firebaseModel.deleteBook(
            bookId = bookId,
            onSuccess = {
                executor.execute {
                    localDb.bookDao.deleteById(bookId)
                    mainHandler.post { onComplete?.invoke(true) }
                }
            },
            onError = { e ->
                Log.e(tag, "Error deleting book: $e")
                mainHandler.post { onComplete?.invoke(false) }
            }
        )
    }
}