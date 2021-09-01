package com.app.TODOapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @ColumnInfo(name = "task") var task: String,
    @ColumnInfo(name = "details") var details: String?,
    @ColumnInfo(name = "checked") var checked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
