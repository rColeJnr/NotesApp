package com.notes.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import com.notes.data.NoteDbo
import com.notes.ui.list.NoteListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val noteDatabase: NoteDatabase
) : ViewModel() {

    private val _note = MutableLiveData<NoteListItem?>()
    val note: LiveData<NoteListItem?> = _note

    private val _navigateBack = MutableLiveData<Unit?>()
    val navigateBack: LiveData<Unit?> = _navigateBack

    private val _invalidNote = MutableLiveData<Unit?>()
    val invalidNote: LiveData<Unit?> = _invalidNote

    private val _noteTitle = MutableLiveData<String>()
    val noteTitle: LiveData<String> = _noteTitle

    private val _noteDetails = MutableLiveData<String>()
    val noteDetails: LiveData<String> = _noteDetails

    fun onGetNote(noteListItem: NoteListItem?) {
        _note.postValue(noteListItem)
        _noteTitle.value = noteListItem?.title
        _noteDetails.value = noteListItem?.content
    }

    fun onSaveNoteClick(title: String, details: String) {
        if (title.isEmpty() || details.isEmpty()) {
            _invalidNote.postValue(Unit)
            return
        }
        val note = _note.value
        viewModelScope.launch(Dispatchers.IO) {
            if (note == null) {
                noteDatabase.noteDao().insertAll(
                    NoteDbo(
                        title = title,
                        content = details,
                        createdAt = LocalDateTime.now(),
                        modifiedAt = LocalDateTime.now()
                    )
                )
            } else {
                noteDatabase.noteDao().updateAll(
                    NoteDbo(
                        id = note.id,
                        title = title,
                        content = details,
                        modifiedAt = LocalDateTime.now(),
                        createdAt = note.createdAt
                    )
                )
            }
        }
        onNavigateBack()
    }

    fun updateNoteTitle(title: String) {
        _noteTitle.value = title
    }


    fun updateNoteDetails(details: String) {
        _noteDetails.value = details
    }

    private fun onNavigateBack() {
        _note.postValue(null)
        _navigateBack.postValue(Unit)
    }
}