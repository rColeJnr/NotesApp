package com.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.data.NoteDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

//@RootScope
@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val noteDatabase: NoteDatabase
) : ViewModel() {

    private val _notes = MutableLiveData<List<NoteListItem>?>()
    val notes: LiveData<List<NoteListItem>?> = _notes

    private val _navigateToNoteCreation = MutableLiveData<Boolean>()
    val navigateToNoteCreation: LiveData<Boolean> = _navigateToNoteCreation

    init {
        updateNoteList()
    }

    private fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.noteDao().getAll().collectLatest { list ->
                _notes.postValue(list.map { it.toNoteListItem() })
            }
        }
    }

    fun onCreateNoteClick() {
        _navigateToNoteCreation.postValue(true)
    }

    fun onAfterCreateNoteClick() {
        _navigateToNoteCreation.postValue(false)
    }

    // delete note
    fun onDeleteNote(noteListItem: NoteListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.noteDao().apply {
                deleteAll(noteListItem.toNoteDbo())
            }
        }
    }

    // reinsert note
    fun onRestoreNote(noteListItem: NoteListItem) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDatabase.noteDao().insertAll(noteListItem.toNoteDbo())
        }
        updateNoteList()
    }

    fun updateNoteList() {
        getNotes()
    }
}