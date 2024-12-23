package com.example.teacher_assistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson")
data class Lesson(
    @PrimaryKey(autoGenerate = true) val idLesson: Int = 0,
    val groupName: String,
    val groupNumber: String,
    val roomNumber: String
)