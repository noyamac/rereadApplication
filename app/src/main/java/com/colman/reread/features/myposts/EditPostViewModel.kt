package com.colman.reread.features.myposts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.model.Book
import com.colman.reread.model.BookRepository

class EditPostViewModel : ViewModel() {

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    fun updateBook(
        id: String,
        title: String,
        author: String,
        priceStr: String,
        description: String,
        summary: String,
        contactPhone: String,
        imageUrl: String,
        sellerName: String,
        sellerEmail: String
    ) {
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val updatedBook = Book(
            id = id,
            title = title,
            author = author,
            price = price,
            description = description,
            summary = summary,
            imageUrl = imageUrl,
            contactPhone = contactPhone,
            sellerName = sellerName,
            sellerEmail = sellerEmail
        )
        BookRepository.updateBook(updatedBook)
        _updateStatus.value = true
    }
}
