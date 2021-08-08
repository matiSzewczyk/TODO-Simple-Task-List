package com.app.TODOapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDao {
    @Insert
    fun insertTask(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAll(): MutableList<Task>

    @Query("DELETE FROM tasks WHERE task = :task")
    fun deleteTask(task: String)

    @Query("UPDATE tasks SET checked = 0")
    fun setAllToUnchecked()

    @Query("UPDATE tasks SET description = :description WHERE task = :task")
    fun changeDescription(task: String, description: String)
}