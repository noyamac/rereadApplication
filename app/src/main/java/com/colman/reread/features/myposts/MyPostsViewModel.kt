package com.colman.reread.features.myposts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.Book

class MyPostsViewModel : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    init {
        loadMockMyPosts()
    }

    private fun loadMockMyPosts() {
        // Mocking books that belong to the "logged in user"
        _books.value = listOf(
            Book(
                id = "2",
                title = "1984",
                author = "George Orwell",
                price = 12.50,
                description = "Good - Minor wear on cover",
                summary = "A dystopian novel about totalitarianism, surveillance, and control.",
                imageUrl = "https://covers.openlibrary.org/b/id/15102551-L.jpg",
                contactPhone = "052-9876543"
            ),
            Book(
                id = "4",
                title = "The Catcher in the Rye",
                author = "J.D. Salinger",
                price = 10.99,
                description = "Acceptable - Yellowed pages",
                summary = "A story about teenage angst and alienation in New York City.",
                imageUrl = "https://covers.openlibrary.org/b/id/8231992-L.jpg",
                contactPhone = "050-0001112"
            )
        )
    }
}
