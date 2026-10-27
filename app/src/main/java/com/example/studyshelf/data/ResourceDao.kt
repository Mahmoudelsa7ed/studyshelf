
package com.example.studyshelf.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction


@Dao
interface ResourceDao {
    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :subjectId")
    suspend fun getSubjectWithResources(
        subjectId: Int
    ): SubjectWithResources?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResource(resource: Resource)

    @Update
    suspend fun updateResource(resource: Resource)

    @Delete
    suspend fun deleteResource(resource: Resource)

    @Query("SELECT * FROM resources WHERE subjectId = :subjectId")
    suspend fun getResourcesBySubject(subjectId: Int): List<Resource>

    @Query(
        "SELECT * FROM resources " +
                "WHERE subjectId = :subjectId AND category = :category"
    )
    suspend fun getResourcesByCategory(
        subjectId: Int,
        category: String
    ): List<Resource>
}
