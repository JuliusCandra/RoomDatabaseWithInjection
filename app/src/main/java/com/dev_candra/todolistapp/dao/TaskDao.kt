package com.dev_candra.todolistapp.dao

import androidx.room.*
import com.dev_candra.todolistapp.entity.SortOrder
import com.dev_candra.todolistapp.entity.Task
import kotlinx.coroutines.flow.Flow

// membuat sebuah kelas DAO
@Dao
interface TaskDao {

    fun getTask(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder){
            SortOrder.BY_NAME -> getTaskSortedByName(query,hideCompleted)
            SortOrder.BY_DATE -> getTaskSortedByCreated(query,hideCompleted)
        }

    @Query("SELECT * FROM tabel_task WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,name")
    fun getTaskSortedByName(searchQuery: String,hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tabel_task WHERE(completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,created")
    fun getTaskSortedByCreated(searchQuery: String,hideCompleted: Boolean): Flow<List<Task>>

    // menambahkan data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taks: Task)

    // mengupdate data
    @Update
    suspend fun update(task: Task)

    // mendelete data
    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM tabel_task WHERE completed = 1")
    suspend fun deleteCompletedTask()

}