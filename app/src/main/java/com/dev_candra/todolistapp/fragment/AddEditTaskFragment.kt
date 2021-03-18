package com.dev_candra.todolistapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dev_candra.todolistapp.R
import com.dev_candra.todolistapp.databinding.FragmentAddEditTaskBinding
import com.dev_candra.todolistapp.util.exhaustive
import com.dev_candra.todolistapp.viewmodel.AddEditTaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_add_edit_task.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditTaskFragment: Fragment(R.layout.fragment_add_edit_task){

    private val viewModel: AddEditTaskViewModel by viewModels()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditTaskBinding.bind(view)
        binding.apply {
            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            check_box_important.jumpDrawablesToCurrentState()
            text_view_date_created.isVisible = viewModel.task != null
            textViewDateCreated.text = "Created: ${viewModel.task?.createdFormatted}"

            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
            }

            fab_save.setOnClickListener {
                viewModel.onSaveClick()
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.addEditTaskEvent.collect {
                    event ->
                    when (event) {
                        is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                            Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_LONG).show()
                        }
                        is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult ->{
                            binding.editTextTaskName.clearFocus()
                            setFragmentResult(
                                "add_edit_request",
                                bundleOf(
                                    "add_edit_result" to event.result
                                )
                            )
                            findNavController().popBackStack()
                        }
                    }.exhaustive
                }
            }

        }
    }

}