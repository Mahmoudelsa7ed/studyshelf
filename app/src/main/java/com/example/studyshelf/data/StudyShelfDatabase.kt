package com.example.studyshelf.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subject::class], version = 1, exportSchema = false)
abstract class StudyShelfDatabase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    //abstract fun resourceDao(): ResourceDao///////
    companion object {
        @Volatile
        private var INSTANCE: StudyShelfDatabase? = null

        fun getInstance(context: Context): StudyShelfDatabase {
            if (INSTANCE == null) {
                synchronized(StudyShelfDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        StudyShelfDatabase::class.java,
                        "studyshelf_db"
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }
    }
}