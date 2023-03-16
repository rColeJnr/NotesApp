package com.notes.ui.details

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.notes.R
import com.notes.databinding.FragmentNoteDetailsBinding
import com.notes.ui.SharedViewModel
import com.notes.ui._base.ViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsFragment : ViewBindingFragment<FragmentNoteDetailsBinding>(
    FragmentNoteDetailsBinding::inflate
) {

    private val viewModel: NoteDetailsViewModel by viewModels()

    // both fragments they receive the same SharedViewModel instance, which is scoped to the parent activity.
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewBindingCreated(
        viewBinding: FragmentNoteDetailsBinding,
        savedInstanceState: Bundle?
    ) {
        super.onViewBindingCreated(viewBinding, savedInstanceState)

        viewBinding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        sharedViewModel.note.observe(
            viewLifecycleOwner
        ) {
            viewModel.onGetNote(it)
        }

        viewModel.note.observe(
            viewLifecycleOwner
        ) {
            viewBinding.etNoteTitle.setText(it?.title ?: getString(R.string.emptyString))
            viewBinding.etNoteDetails.setText(it?.content ?: getString(R.string.emptyString))
        }

        viewBinding.btnSaveNote.setOnClickListener {
            viewModel.onSaveNoteClick(
                viewBinding.etNoteTitle.text.toString(),
                viewBinding.etNoteDetails.text.toString()
            )
        }

        viewModel.invalidNote.observe(
            viewLifecycleOwner
        ) {
            Toast.makeText(requireContext(), getString(R.string.invalidNote), Toast.LENGTH_SHORT)
                .show()
        }

        viewModel.navigateBack.observe(
            viewLifecycleOwner
        ) {
            sharedViewModel.onNewNoteAdded()
            activity?.onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause()
        sharedViewModel.setNoteValue(null)
    }

}
