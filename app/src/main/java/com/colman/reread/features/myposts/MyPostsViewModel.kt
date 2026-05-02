package com.colman.reread.features.myposts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.colman.reread.R
import com.colman.reread.model.Book
import com.colman.reread.data.repository.BookRepository
import com.google.firebase.auth.FirebaseAuth

class MyPostsViewModel : ViewModel() {

    sealed class DeleteStatus {
        data class Success(val bookTitle: String) : DeleteStatus()
        data class Error(val messageResId: Int) : DeleteStatus()
        object Idle : DeleteStatus()
    }

    private val _deleteStatus = MutableLiveData<DeleteStatus>(DeleteStatus.Idle)
    val deleteStatus: LiveData<DeleteStatus> = _deleteStatus

    val books: LiveData<List<Book>> = BookRepository.books.map { allBooks ->
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        allBooks.filter { it.sellerEmail == currentUserEmail }
    }

    fun deleteBook(book: Book) {
        BookRepository.deleteBook(book.id) { success ->
            if (success) {
                _deleteStatus.value = DeleteStatus.Success(book.title)
            } else {
                _deleteStatus.value = DeleteStatus.Error(R.string.error_delete_book)
            }
        }
    }

    fun resetDeleteStatus() {
        _deleteStatus.value = DeleteStatus.Idle
    }
}
