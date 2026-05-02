package com.colman.reread.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.Book
import com.colman.reread.data.repository.BookRepository

class HomeViewModel : ViewModel() {

    private val _searchQuery = MutableLiveData<String>("")
    
    private val _books = MediatorLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    init {
        _books.addSource(BookRepository.shared.books) { allBooks ->
            filterBooks(allBooks, _searchQuery.value ?: "")
        }
        
        _books.addSource(_searchQuery) { query ->
            filterBooks(BookRepository.shared.books.value ?: emptyList(), query)
        }
    }

    private fun filterBooks(allBooks: List<Book>, query: String) {
        val result = if (query.isEmpty()) {
            allBooks
        } else {
            allBooks.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true) 
            }
        }
        _books.value = ArrayList(result)
    }

    fun filterBooks(query: String) {
        _searchQuery.value = query
    }
}
