package com.example.studyshelf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubjectDao {

    @Insert
    suspend fun insertSubject(subject: Subject)
    @Query("SELECT * FROM subjects ORDER BY id DESC")
    suspend fun getAllSubjects(): List<Subject>
    @Query("SELECT * FROM subjects WHERE id = :subjectId LIMIT 1")
    suspend fun getSubjectById(subjectId: Int): Subject?
    @Query("SELECT * FROM subjects WHERE name LIKE '%' || :searchQuery || '%' ORDER BY id DESC")
    suspend fun searchSubjects(searchQuery: String): List<Subject>
    @Update
    suspend fun updateSubject(subject: Subject)
    @Delete
    suspend fun deleteSubject(subject: Subject)
}