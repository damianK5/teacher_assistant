package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Query("SELECT * FROM student")
    suspend fun getAll(): List<Student>

    @Query("SELECT * FROM student WHERE idStudent = :idStudent")
    suspend fun getById(idStudent: Int): Student?

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("DELETE FROM student")
    suspend fun deleteAll()

    @Query("delete from sqlite_sequence where name='student'")
    suspend fun resetId()
}