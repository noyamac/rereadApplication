package com.colman.reread.features.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.Book

class HomeViewModel : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    private val allBooks = listOf(
        Book(
            id = "1",
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            price = 15.99,
            description = "Like New - Hardcover",
            summary = "A story of wealth, love, and the American Dream in the 1920s.",
            imageUrl = "https://covers.openlibrary.org/b/id/7222246-L.jpg"
        ),
        Book(
            id = "2",
            title = "1984",
            author = "George Orwell",
            price = 12.50,
            description = "Good - Minor wear on cover",
            summary = "A dystopian novel about totalitarianism, surveillance, and control.",
            imageUrl = "https://covers.openlibrary.org/b/id/15102551-L.jpg"
        ),
        Book(
            id = "3",
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            price = 14.00,
            description = "New - Paperback",
            summary = "A classic tale of justice and childhood in the Deep South.",
            imageUrl = "https://covers.openlibrary.org/b/id/8226191-L.jpg"
        ),
        Book(
            id = "4",
            title = "The Catcher in the Rye",
            author = "J.D. Salinger",
            price = 10.99,
            description = "Acceptable - Yellowed pages",
            summary = "A story about teenage angst and alienation in New York City.",
            imageUrl = "https://covers.openlibrary.org/b/id/8231992-L.jpg"
        ),
        Book(
            id = "5",
            title = "Pride and Prejudice",
            author = "Jane Austen",
            price = 9.99,
            description = "Very Good - Pocket edition",
            summary = "A romantic masterpiece about manners, marriage, and morality.",
            imageUrl = "https://covers.openlibrary.org/b/id/14578132-L.jpg"
        )
    )

    init {
        _books.value = allBooks
    }

    fun filterBooks(query: String) {
        if (query.isEmpty()) {
            _books.value = allBooks
        } else {
            _books.value = allBooks.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true) 
            }
        }
    }
}
