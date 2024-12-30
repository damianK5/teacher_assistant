package com.example.teacher_assistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "studentLesson")
data class StudentLesson(
    @PrimaryKey(autoGenerate = true) val idStudentLesson: Int = 0,
    val idStudent: Int,
    val idLesson: Int
)