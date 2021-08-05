package com.example.TODOapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed")
data class Completed(
    @ColumnInfo(name = "task") val task: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "checked") var checked: Boolean = false
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}