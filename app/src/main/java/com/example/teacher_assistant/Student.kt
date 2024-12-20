package com.example.teacher_assistant

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student")
data class Student(
    @PrimaryKey(autoGenerate = true) val idStudent: Int = 0,
    val firstName: String,
    val lastName: String,
    val studentNumber: String
)