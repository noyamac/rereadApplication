package com.colman.reread.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.colman.reread.model.Book

@Dao
interface BookDao {
    @Query("SELECT * FROM Book")
    fun getAllBooks(): LiveData<List<Book>>

    @Query("SELECT * FROM Book WHERE id = :bookId")
    fun getBookById(bookId: String): LiveData<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(vararg books: Book )

    @Query("DELETE FROM Book WHERE id = :bookId")
    fun deleteById(bookId: String)
}