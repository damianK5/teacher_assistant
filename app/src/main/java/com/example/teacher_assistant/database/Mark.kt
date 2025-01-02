package com.example.teacher_assistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mark")
data class Mark(
    @PrimaryKey(autoGenerate = true) val idMark: Int = 0,
    val idStudent: Int,
    val idLesson: Int,
    val value: Double,
    val type: String,
    val description: String
)