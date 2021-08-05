package com.example.projectx

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CompletedDao {
    @Insert
    fun addCompleted(completed: Completed)

    @Query("SELECT * FROM completed")
    fun getAll(): MutableList<Completed>

    @Query("DELETE FROM completed")
    fun deleteAllTasks()

    @Query("DELETE FROM completed WHERE checked = 1")
    fun deleteSelected()

    @Query("UPDATE completed SET checked = :isChecked WHERE task = :task")
    fun changeChecked(task: String, isChecked: Boolean)

    @Query("UPDATE completed SET checked = 1")
    fun setAllToChecked()

    @Query("INSERT INTO tasks SELECT * FROM completed WHERE checked = 1")
    fun moveToTaskList()
}