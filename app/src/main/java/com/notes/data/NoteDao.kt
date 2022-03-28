package com.notes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<NoteDbo>>

    @Insert
    fun insertAll(vararg notes: NoteDbo)

    @Update
    fun updateAll(vararg notes: NoteDbo)

    @Delete
    fun deleteAll(vararg notes: NoteDbo)

}