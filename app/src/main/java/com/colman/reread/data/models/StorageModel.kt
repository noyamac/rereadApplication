package com.colman.reread.data.models

import android.graphics.Bitmap
import com.colman.reread.base.StringCompletion

class StorageModel {

    private val firebaseStorage = FirebaseStorageModel()

    fun uploadImage(folderPath: String, image: Bitmap, completion: StringCompletion) {
        firebaseStorage.uploadImage(folderPath, image, completion)
    }
}
