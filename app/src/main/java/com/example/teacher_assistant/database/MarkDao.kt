package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mark: Mark)

    @Query("SELECT * FROM mark")
    suspend fun getAll(): List<Mark>

    @Query("SELECT * FROM mark WHERE idMark = :idMark")
    suspend fun getById(idMark: Int): Mark?

    @Update
    suspend fun update(mark: Mark)

    @Delete
    suspend fun delete(mark: Mark)

    @Query("DELETE FROM student")
    suspend fun deleteAll()

    @Query("SELECT * FROM mark WHERE idLesson = :idLesson")
    suspend fun getByLessonId(idLesson: Int): List<Mark>

    @Query("SELECT * FROM mark WHERE idStudent = :idStudent")
    suspend fun getByStudentId(idStudent: Int): List<Mark>

    @Query("SELECT * FROM mark WHERE idStudent = :idStudent AND idLesson = :idLesson")
    suspend fun getByStudentAndLessonId(idStudent: Int, idLesson: Int): List<Mark>
}