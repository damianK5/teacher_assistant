package com.example.teacher_assistant.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Student::class,
        Lesson::class,
        CustomTableRow::class,
        LessonInSchedule::class,
        StudentLesson::class],
    version = 8)

abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun lessonDao(): LessonDao
    abstract fun customTableRowDao(): CustomTableRowDao
    abstract fun lessonInScheduleDao(): LessonInScheduleDao
    abstract fun studentLessonDao(): StudentLessonDao

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