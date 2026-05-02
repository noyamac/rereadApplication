package com.colman.reread.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.colman.reread.model.Book
import com.colman.reread.model.User

@Database(entities = [Book::class, User::class], version = 2)
abstract class AppLocalDbRepository: RoomDatabase() {
    abstract val bookDao: BookDao
    abstract val userDao: UserDao
}