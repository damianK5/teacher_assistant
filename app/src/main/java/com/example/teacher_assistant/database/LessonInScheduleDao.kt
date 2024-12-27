package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LessonInScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lessonInSchedule: LessonInSchedule)

    @Query("SELECT * FROM lessonInSchedule")
    suspend fun getAll(): List<LessonInSchedule>

    @Query("SELECT * FROM lessonInSchedule WHERE idLessonInSchedule = :idLessonInSchedule")
    suspend fun getById(idLessonInSchedule: Int): LessonInSchedule?

    @Update
    suspend fun update(lessonInSchedule: LessonInSchedule)

    @Delete
    suspend fun delete(lessonInSchedule: LessonInSchedule)

    @Query("DELETE FROM lessonInSchedule")
    suspend fun deleteAll()
}