package com.colman.reread.dao

import androidx.room.Room
import com.colman.reread.MyApplication

object AppLocalDb {
    val db: AppLocalDbRepository by lazy{

        val context = MyApplication.appContext
            ?: throw IllegalStateException("Context is null")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "books.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }
}