package com.notes.data

import androidx.room.*

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): List<NoteDbo>

    @Insert
    fun insertAll(vararg notes: NoteDbo)

    @Update
    fun updateAll(vararg notes: NoteDbo)

    @Delete
    fun deleteAll(vararg notes: NoteDbo)

}