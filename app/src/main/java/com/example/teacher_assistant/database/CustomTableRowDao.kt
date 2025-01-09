package com.example.teacher_assistant.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CustomTableRowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tableRow: CustomTableRow)

    @Query("SELECT * FROM customTableRow")
    suspend fun getAll(): List<CustomTableRow>

    @Query("SELECT * FROM customTableRow WHERE idCustomTableRow = :idCustomTableRow")
    suspend fun getById(idCustomTableRow: Int): CustomTableRow?

    @Update
    suspend fun update(customTableRow: CustomTableRow)

    @Delete
    suspend fun delete(customTableRow: CustomTableRow)

    @Query("DELETE FROM customTableRow")
    suspend fun deleteAll()

    @Query("delete from sqlite_sequence where name='customTableRow'")
    suspend fun resetId()
}