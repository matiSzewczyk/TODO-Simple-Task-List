package com.example.projectx

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @ColumnInfo(name = "task") val task: String
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
