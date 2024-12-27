package com.example.teacher_assistant.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessonInSchedule")
data class LessonInSchedule(
    @PrimaryKey(autoGenerate = true) val idLessonInSchedule: Int = 0,
    val idLesson: Int,
    val columnIndex: Int,
    val rowIndex: Int
)