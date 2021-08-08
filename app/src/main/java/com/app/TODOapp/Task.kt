package com.app.TODOapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @ColumnInfo(name = "task") val task: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "checked") var checked: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
