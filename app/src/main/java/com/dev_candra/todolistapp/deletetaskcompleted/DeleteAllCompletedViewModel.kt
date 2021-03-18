package com.dev_candra.todolistapp.deletetaskcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.dev_candra.todolistapp.dao.TaskDao
import com.dev_candra.todolistapp.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val aplication : CoroutineScope
) : ViewModel(){
    fun onConfirmClick() = aplication.launch {
        taskDao.deleteCompletedTask()
    }
}