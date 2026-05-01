package com.colman.reread.features.sell

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.R
import com.colman.reread.data.models.StorageModel
import com.colman.reread.data.repository.UserRepository
import com.colman.reread.model.Book

class SellViewModel : ViewModel() {

    private val userRepository = UserRepository.shared
    private val storageModel = StorageModel()

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
        image: Bitmap?
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

        if (image == null) {
            _postStatus.value = PostStatus.Error(R.string.error_select_book_image)
            return
        }

        val bookId = System.currentTimeMillis().toString()

        userRepository.getCurrentUser { user ->
            storageModel.uploadImage(
                folderPath = "books/$bookId",
                image = image,
                completion = { uploadedImageUrl ->
                    if (uploadedImageUrl == null) {
                        _postStatus.value = PostStatus.Error(R.string.error_upload_book_image)
                        return@uploadImage
                    }
                    val newBook = Book(
                        id = bookId,
                        title = title,
                        author = author,
                        price = price,
                        description = description,
                        summary = summary,
                        imageUrl = uploadedImageUrl,
                        contactPhone = contactPhone,
                        sellerName = user?.name ?: "",
                        sellerEmail = user?.email ?: ""
                    )

                    // TODO: In the future, save newBook to a repository/database
                    _postStatus.value = PostStatus.Success
                }
            )
        }
    }

    fun resetStatus() {
        _postStatus.value = PostStatus.Idle
    }
}
