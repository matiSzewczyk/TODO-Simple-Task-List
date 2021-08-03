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

    @Query("DELETE FROM completed WHERE task = :task")
    fun deleteTask(task: String)

    @Query("DELETE FROM completed")
    fun deleteAllTasks()
}