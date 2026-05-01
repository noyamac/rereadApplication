package com.colman.reread.features.myposts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.colman.reread.model.Book
import com.colman.reread.model.BookRepository
import com.colman.reread.model.UserRepository

class MyPostsViewModel : ViewModel() {

    val books: LiveData<List<Book>> = BookRepository.books.map { allBooks ->
        allBooks.filter { it.sellerEmail == UserRepository.currentUser.email }
    }

    fun deleteBook(book: Book) {
        BookRepository.deleteBook(book.id)
    }
}
