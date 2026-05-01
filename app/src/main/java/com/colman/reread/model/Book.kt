package com.colman.reread.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: String,
    val title: String,
    val author: String,
    val price: Double,
    val description: String,
    val summary: String,
    val imageUrl: String,
    val contactPhone: String,
    val sellerName: String = "",
    val sellerEmail: String = ""
) : Parcelable {

    val toJson: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to id,
            TITLE_KEY to title,
            AUTHOR_KEY to author,
            PRICE_KEY to price,
            DESCRIPTION_KEY to description,
            SUMMARY_KEY to summary,
            IMAGE_URL_KEY to imageUrl,
            CONTACT_PHONE_KEY to contactPhone,
            SELLER_NAME_KEY to sellerName,
            SELLER_EMAIL_KEY to sellerEmail,
            LAST_UPDATED_KEY to Timestamp.now()
        )

    companion object {
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val ID_KEY = "id"
        const val TITLE_KEY = "title"
        const val AUTHOR_KEY = "author"
        const val PRICE_KEY = "price"
        const val DESCRIPTION_KEY = "description"
        const val SUMMARY_KEY = "summary"
        const val IMAGE_URL_KEY = "imageUrl"
        const val CONTACT_PHONE_KEY = "contactPhone"
        const val SELLER_NAME_KEY = "sellerName"
        const val SELLER_EMAIL_KEY = "sellerEmail"

        fun fromJson(json: Map<String, Any>): Book? {
            val id = json[ID_KEY] as? String ?: return null
            val title = json[TITLE_KEY] as? String ?: ""
            val author = json[AUTHOR_KEY] as? String ?: ""
            val price = (json[PRICE_KEY] as? Number)?.toDouble() ?: 0.0
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val summary = json[SUMMARY_KEY] as? String ?: ""
            val imageUrl = json[IMAGE_URL_KEY] as? String ?: ""
            val contactPhone = json[CONTACT_PHONE_KEY] as? String ?: ""
            val sellerName = json[SELLER_NAME_KEY] as? String ?: ""
            val sellerEmail = json[SELLER_EMAIL_KEY] as? String ?: ""

            return Book(
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
        }
    }
}
