
package com.example.studyshelf.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class Resource(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val subjectId: Int,
    val title: String,
    val category: String,
    val url: String = "",
    val description: String = ""
)
