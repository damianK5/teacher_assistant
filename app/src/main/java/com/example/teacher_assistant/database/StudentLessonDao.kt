package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentLessonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentLesson)

    @Query("SELECT * FROM studentLesson")
    suspend fun getAll(): List<StudentLesson>

    @Query("SELECT * FROM studentLesson WHERE idStudentLesson = :idStudentLesson")
    suspend fun getById(idStudentLesson: Int): StudentLesson?

    @Update
    suspend fun update(studentLesson: StudentLesson)

    @Delete
    suspend fun delete(studentLesson: StudentLesson)

    @Query("DELETE FROM studentLesson")
    suspend fun deleteAll()

    @Query("SELECT * FROM studentLesson WHERE idLesson = :idLesson")
    suspend fun getByLessonId(idLesson: Int): List<StudentLesson>

    @Query("SELECT * FROM studentLesson WHERE idStudent = :idStudent")
    suspend fun getByStudentId(idStudent: Int): List<StudentLesson>
}