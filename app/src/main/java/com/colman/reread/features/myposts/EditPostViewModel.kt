package com.colman.reread.features.myposts

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.colman.reread.R
import com.colman.reread.data.models.StorageModel
import com.colman.reread.model.Book
import com.colman.reread.model.BookRepository

class EditPostViewModel : ViewModel() {

    private val storageModel = StorageModel()

    sealed class UpdateStatus {
        object Success : UpdateStatus()
        data class Error(val messageResId: Int) : UpdateStatus()
        object Idle : UpdateStatus()
    }

    private val _updateStatus = MutableLiveData<UpdateStatus>(UpdateStatus.Idle)
    val updateStatus: LiveData<UpdateStatus> = _updateStatus

    fun updateBook(
        id: String,
        title: String,
        author: String,
        priceStr: String,
        description: String,
        summary: String,
        contactPhone: String,
        imageUrl: String,
        image: Bitmap?,
        sellerName: String,
        sellerEmail: String
    ) {
        if (title.isBlank() || author.isBlank() || priceStr.isBlank() ||
            description.isBlank() || summary.isBlank() || contactPhone.isBlank()
        ) {
            _updateStatus.value = UpdateStatus.Error(R.string.error_empty_fields)
            return
        }

        val price = priceStr.toDoubleOrNull() ?: 0.0
        if (price <= 0.0) {
            _updateStatus.value = UpdateStatus.Error(R.string.error_invalid_price)
            return
        }

        fun saveBook(finalImageUrl: String) {
            val updatedBook = Book(
                id = id,
                title = title,
                author = author,
                price = price,
                description = description,
                summary = summary,
                imageUrl = finalImageUrl,
                contactPhone = contactPhone,
                sellerName = sellerName,
                sellerEmail = sellerEmail
            )
            BookRepository.updateBook(updatedBook)
            _updateStatus.value = UpdateStatus.Success
        }

        if (image != null) {
            storageModel.uploadImage(
                folderPath = "books/$id",
                image = image,
                completion = { uploadedImageUrl ->
                    if (uploadedImageUrl == null) {
                        _updateStatus.value = UpdateStatus.Error(R.string.error_upload_book_image)
                        return@uploadImage
                    }
                    saveBook(uploadedImageUrl)
                }
            )
        } else {
            saveBook(imageUrl)
        }
    }
}
