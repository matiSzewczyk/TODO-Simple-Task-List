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

    @Query("UPDATE tasks SET details = :details WHERE task = :task")
    fun changeDetails(task: String, details: String)

    @Query("UPDATE tasks SET task = :task WHERE task = :prevTask")
    fun changeTitle(prevTask: String, task: String)

    @Query("SELECT task FROM tasks WHERE task = :task")
    fun getTaskText(task: String): String

    @Query("SELECT details FROM tasks WHERE task = :task")
    fun getDetailsText(task: String): String
}