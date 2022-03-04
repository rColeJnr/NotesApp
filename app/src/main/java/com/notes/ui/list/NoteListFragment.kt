package com.notes.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.recyclerview.widget.SortedListAdapterCallback
import com.google.android.material.snackbar.Snackbar
import com.notes.R
import com.notes.databinding.FragmentNoteListBinding
import com.notes.databinding.ListItemNoteBinding
import com.notes.ui.SharedViewModel
import com.notes.ui._base.FragmentNavigator
import com.notes.ui._base.ViewBindingFragment
import com.notes.ui._base.findImplementationOrThrow
import com.notes.ui.details.NoteDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : ViewBindingFragment<FragmentNoteListBinding>(
    FragmentNoteListBinding::inflate
) {

    private val viewModel: NoteListViewModel by viewModels()

    // both fragments they receive the same SharedViewModel instance, which is scoped to the parent activity.
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val recyclerViewAdapter = RecyclerViewAdapter(
        onItemLongClicked = this::onNoteLongClick,
        onItemClicked = this::onNoteClick
    )

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteListBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        viewBinding.list.apply {
            adapter = recyclerViewAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayout.VERTICAL
                )
            )
        }
        viewBinding.createNoteButton.setOnClickListener {
            viewModel.onCreateNoteClick()
        }

        viewModel.notes.observe(
            viewLifecycleOwner
        ) {
            if (it != null) {
                recyclerViewAdapter.setItems(it)
            }
        }

        viewModel.navigateToNoteCreation.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                findImplementationOrThrow<FragmentNavigator>()
                    .navigateTo(
                        NoteDetailsFragment()
                    )
                viewModel.onAfterCreateNoteClick()
            }
        }

        sharedViewModel.newNoteAdded.observe(
            viewLifecycleOwner
        ) {
            viewModel.updateNoteList()
        }
    }

    // navigate to detailsFragment on note click
    private fun onNoteClick(position: Int) {
        sharedViewModel.setNoteValue(getNote(position))
        viewModel.onCreateNoteClick()
    }

    // delete note on long click
    private fun onNoteLongClick(position: Int) {
        deleteNote(position)
    }

    private fun deleteNote(position: Int) {
        val noteToDelete = getNote(position)
        // remove note from database
        viewModel.onDeleteNote(noteToDelete)
        // remove note from recyclerView sortedList
        recyclerViewAdapter.removeItem(position)
        // show option to restore deleted note
        restoreDeletedNote(noteToDelete)
    }

    // show snackbar with option to restore deleted note
    private fun restoreDeletedNote(noteToDelete: NoteListItem) {
        Snackbar.make(
            requireContext(),
            viewBinding?.root!!,
            getString(R.string.noteDeleted),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                viewModel.onRestoreNote(noteToDelete)
            }
            .show()
    }

    // take position from recyclerView onClick methods and get the corresponding item from the list
    private fun getNote(position: Int): NoteListItem =
        recyclerViewAdapter.getItem(position)

    private class RecyclerViewAdapter(
        private val onItemClicked: (Int) -> Unit,
        private val onItemLongClicked: (Int) -> Unit
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

        // A Sorted list implementation that can keep items in order and also
        // notify for changes in the list such that it can be bound to a RecyclerView.Adapter.
        private val sortedList = SortedList(NoteListItem::class.java,
            object : SortedListAdapterCallback<NoteListItem>(this) {

                override fun compare(note1: NoteListItem, note2: NoteListItem): Int {
                    return note2.modifiedAt.compareTo(note1.modifiedAt)
                }

                override fun areItemsTheSame(
                    oldItem: NoteListItem,
                    newItem: NoteListItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: NoteListItem,
                    newItem: NoteListItem
                ): Boolean {
                    return oldItem.modifiedAt == newItem.modifiedAt
                }
            }
        )

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) = ViewHolder(
            ListItemNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClicked,
            onItemLongClicked
        )

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {
            holder.bind(sortedList[position])
        }

        override fun getItemCount() = sortedList.size()

        fun setItems(
            items: List<NoteListItem>
        ) {
            // only update necessary fields
            sortedList.replaceAll(items)
        }

        // get the item selected on recycler view on click methods
        fun getItem(position: Int): NoteListItem =
            sortedList.get(position)

        fun removeItem(position: Int) {
            sortedList.removeItemAt(position)
        }

        inner class ViewHolder(
            private val binding: ListItemNoteBinding,
            private val onItemClicked: (Int) -> Unit,
            private val onItemLongClicked: (Int) -> Unit
        ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            fun bind(
                noteListItem: NoteListItem
            ) {
                binding.titleLabel.text = noteListItem.title
                binding.contentLabel.text = noteListItem.content
            }

            override fun onClick(v: View?) {
                val position = adapterPosition
                onItemClicked(position)
            }

            override fun onLongClick(v: View?): Boolean {
                val position = adapterPosition
                onItemLongClicked(position)
                return true
            }

        }

    }

}
