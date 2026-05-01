package com.colman.reread.features.sell

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.R
import com.colman.reread.data.repository.UserRepository
import com.colman.reread.model.Book

class SellViewModel : ViewModel() {

    private val userRepository = UserRepository.shared

    private val _postStatus = MutableLiveData<PostStatus>()
    val postStatus: LiveData<PostStatus> = _postStatus

    sealed class PostStatus {
        object Success : PostStatus()
        data class Error(val messageResId: Int) : PostStatus()
        object Idle : PostStatus()
    }

    fun postBook(
        title: String,
        author: String,
        priceStr: String,
        description: String,
        summary: String,
        contactPhone: String,
        imageUrl: String
    ) {
        if (title.isBlank() || author.isBlank() || priceStr.isBlank() || 
            description.isBlank() || summary.isBlank() || contactPhone.isBlank()) {
            _postStatus.value = PostStatus.Error(R.string.error_empty_fields)
            return
        }

        val price = priceStr.toDoubleOrNull()
        if (price == null) {
            _postStatus.value = PostStatus.Error(R.string.error_invalid_price)
            return
        }

        userRepository.getCurrentUser { user ->
            val newBook = Book(
                id = System.currentTimeMillis().toString(),
                title = title,
                author = author,
                price = price,
                description = description,
                summary = summary,
                imageUrl = imageUrl.ifBlank { "" },
                contactPhone = contactPhone,
                sellerName = user?.name ?: "",
                sellerEmail = user?.email ?: ""
            )

            // TODO: In the future, save newBook to a repository/database
            _postStatus.value = PostStatus.Success
        }
    }

    fun resetStatus() {
        _postStatus.value = PostStatus.Idle
    }
}
