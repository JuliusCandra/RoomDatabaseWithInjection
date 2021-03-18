package com.dev_candra.todolistapp.deletetaskcompleted

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

class DeleteAllCompeltedDialogFragment : DialogFragment(){

    private val viewModel : DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you really want to delete all complete task ?")
            .setNegativeButton("cancel",null)
            .setPositiveButton("Yes"){_,_ ->
                // Call viewmodel
                viewModel.onConfirmClick()
            }
            .create()
}