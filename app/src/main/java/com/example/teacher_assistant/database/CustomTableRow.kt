package com.example.teacher_assistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customTableRow")
data class CustomTableRow(
    @PrimaryKey(autoGenerate = true) val idCustomTableRow: Int = 0,
    val lessonNumber: Int,
    val startHour: String,
    val endHour: String
)