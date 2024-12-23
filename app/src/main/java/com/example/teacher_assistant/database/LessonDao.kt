package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lesson)

    @Query("SELECT * FROM lesson")
    suspend fun getAll(): List<Lesson>

    @Query("SELECT * FROM lesson WHERE idLesson = :idLesson")
    suspend fun getById(idLesson: Int): Lesson?

    @Update
    suspend fun update(lesson: Lesson)

    @Delete
    suspend fun delete(lesson: Lesson)

    @Query("DELETE FROM lesson")
    suspend fun deleteAll()
}