package com.dev_candra.todolistapp.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.dev_candra.todolistapp.ADD_TASK_RESULT_OK
import com.dev_candra.todolistapp.EDIT_TASK_RESULT_OK
import com.dev_candra.todolistapp.dao.TaskDao
import com.dev_candra.todolistapp.entity.PreferenceManager
import com.dev_candra.todolistapp.entity.SortOrder
import com.dev_candra.todolistapp.entity.Task
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferenceManager: PreferenceManager,
    @Assisted private val state: SavedStateHandle
): ViewModel(){
    // membuat sebuah query
    val searchQuery = state.getLiveData("searchQuery","")

    val preferenceFlow = preferenceManager.preferencesFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

//    // mensorted berdasarkan tanggal
//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    // mensorted berdasarkan nama
//    val hideCompleted = MutableStateFlow(false)

    // Mencari berdasarkan query
    private val taskFlow = combine(
        searchQuery.asFlow(),
       preferenceFlow
    ){query,filterPreferences ->
        Pair(query,filterPreferences)
    }.flatMapLatest {(query,filterPreferences) ->
        taskDao.getTask(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
    }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferenceManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferenceManager.updateHighCompleted(hideCompleted)
    }

    // membuat data menjadi live data
    fun onTaskSeleceted(task: Task) = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigationToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task,isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task:Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task Update")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch{
        taskEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onDeleteAllCompleted() = viewModelScope.launch {
        taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }


    sealed class TaskEvent {
        object NavigateToAddTaskScreen: TaskEvent()
        data class NavigationToEditTaskScreen(val task: Task): TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task): TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String): TaskEvent()
        object NavigateToDODeleteAllCompeltedScreen : TaskEvent()
    }
}
