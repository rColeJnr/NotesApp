package com.notes.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.notes.ui.list.NoteListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

//@RootScope
@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    // shared note selected from noteList recyclerView with noteDetails
    val note = MutableLiveData<NoteListItem?>()

    fun setNoteValue(noteListItem: NoteListItem?) {
        note.postValue(noteListItem)
    }

    // send message that a note has been created or modified from noteDetails to noteList
    val newNoteAdded = MutableLiveData<Unit?>()

    fun onNewNoteAdded() {
        newNoteAdded.postValue(Unit)
    }

}