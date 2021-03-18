package com.dev_candra.todolistapp.fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.todolistapp.R
import com.dev_candra.todolistapp.adapter.TaskAdapter
import com.dev_candra.todolistapp.databinding.FragmentTaskBinding
import com.dev_candra.todolistapp.entity.SortOrder
import com.dev_candra.todolistapp.entity.Task
import com.dev_candra.todolistapp.util.exhaustive
import com.dev_candra.todolistapp.util.onQueryTextChanged
import com.dev_candra.todolistapp.viewmodel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TaskFragment: Fragment(R.layout.fragment_task),TaskAdapter.OnItemClickListener
{
    private val viewModel : TaskViewModel by viewModels()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTaskBinding.bind(view)

        val taskAdapter = TaskAdapter(this)

        binding.recyclerViewTask.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = taskAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onTaskSwiped(task)
            }
        }).attachToRecyclerView(recycler_view_task)

        fab_add_task.setOnClickListener{
            viewModel.onAddNewTaskClick()
        }

        setFragmentResultListener("add_edit_request"){_, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }
        

        viewModel.tasks.observe(viewLifecycleOwner){
            taskAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.taskEvent.collect {event ->
                    when(event){
                        is TaskViewModel.TaskEvent.ShowUndoDeleteTaskMessage -> {
                            Snackbar.make(requireView(),"Task Dihapus",Snackbar.LENGTH_LONG)
                                .setAction("UNDO"){
                                    viewModel.onUndoDeleteClick(event.task)
                                }.show()
                        }
                        is TaskViewModel.TaskEvent.NavigateToAddTaskScreen -> {
                            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(null,"New Task")
                            findNavController().navigate(action)
                        }
                        is TaskViewModel.TaskEvent.NavigationToEditTaskScreen -> {
                            val action = TaskFragmentDirections.actionTaskFragmentToAddEditTaskFragment(event.task,"Edit Task")
                            findNavController().navigate(action)
                        }
                        is TaskViewModel.TaskEvent.ShowTaskSavedConfirmationMessage -> {
                            Snackbar.make(requireView(),event.msg,Snackbar.LENGTH_SHORT).show()
                        }
                        is TaskViewModel.TaskEvent.NavigateToDODeleteAllCompeltedScreen -> {
                            val action = TaskFragmentDirections.actionGlobalDeleteAllCompeltedDialogFragment()
                            findNavController().navigate(action)
                        }
                    }.exhaustive
                }
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu,menu)
        val searchItems = menu.findItem(R.id.action_search)
        searchView = searchItems.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItems.expandActionView()
            searchView.setQuery(pendingQuery,false)
        }

        searchView.onQueryTextChanged {
            // Update search query
            viewModel.searchQuery.value = it
        }
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_complete_task).isChecked =
                viewModel.preferenceFlow.first().hideCompleted
        }
    }

    // Menghandle action dari menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       return when(item.itemId){
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }

           R.id.action_sort_by_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
               true
           }
           R.id.action_hide_complete_task -> {
               item.isChecked = !item.isChecked
               viewModel.onHideCompletedClick(item.isChecked)
               true
           }
           R.id.action_delete_all_completed_task -> {
               viewModel.onDeleteAllCompleted()
               true
           }
           else -> super.onOptionsItemSelected(item)
       }
    }

    override fun onItemClick(task: Task) {
        viewModel.onTaskSeleceted(task)
    }


    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task,isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}