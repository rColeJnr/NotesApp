//package com.notes.di
//
//import com.notes.ui.SharedViewModel
//import com.notes.ui.details.NoteDetailsViewModel
//import com.notes.ui.list.NoteListViewModel
//import dagger.Component
//
//@RootScope
//@Component(
//    dependencies = [
//        AppComponent::class,
//    ],
//    modules = [
//    ]
//)
//interface RootComponent {
//
//    @Component.Factory
//    interface Factory {
//        fun create(
//            appComponent: AppComponent
//        ): RootComponent
//    }
//
//    fun getNoteListViewModel(): NoteListViewModel
//    fun getNoteDetailsViewModel(): NoteDetailsViewModel
//    fun getSharedViewModel(): SharedViewModel
//
//}