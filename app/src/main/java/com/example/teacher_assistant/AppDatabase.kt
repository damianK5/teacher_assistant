package com.example.wydarzenieuczestnik

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.teacher_assistant.Student
import com.example.teacher_assistant.StudentDao

@Database(entities = [Student::class /*, Wydarzenie::class, WydarzenieUczestnik::class */], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    //abstract fun wydarzenieDao(): WydarzenieDao
    //abstract fun wydarzenieUczestnikDao(): WydarzenieUczestnikDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "teacher_assistant_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}