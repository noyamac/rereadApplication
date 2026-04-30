package com.colman.reread.features.sell

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.Book

class SellViewModel : ViewModel() {

    private val _postStatus = MutableLiveData<PostStatus>()
    val postStatus: LiveData<PostStatus> = _postStatus

    sealed class PostStatus {
        object Success : PostStatus()
        data class Error(val message: String) : PostStatus()
        object Idle : PostStatus()
    }

    fun postBook(title: String, author: String, priceStr: String, description: String, summary: String, imageUrl: String) {
        if (title.isBlank() || author.isBlank() || priceStr.isBlank() || description.isBlank() || summary.isBlank()) {
            _postStatus.value = PostStatus.Error("Please fill all required fields")
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null) {
            _postStatus.value = PostStatus.Error("Please enter a valid price")
            return
        }

        // TODO: In the future, this is where we will call the Repository to save to Firebase/DB
        val newBook = Book(
            id = System.currentTimeMillis().toString(),
            title = title,
            author = author,
            price = price,
            description = description,
            summary = summary,
            imageUrl = imageUrl.ifBlank { "" } // Adapter handles empty URL with default image
        )

        _postStatus.value = PostStatus.Success
    }

    fun resetStatus() {
        _postStatus.value = PostStatus.Idle
    }
}
