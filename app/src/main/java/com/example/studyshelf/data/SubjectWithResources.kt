
package com.example.studyshelf.data

import androidx.room.Embedded
import androidx.room.Relation

data class SubjectWithResources(
  @Embedded
  val subject: Subject,

  @Relation(
    parentColumn = "id",
    entityColumn = "subjectId"
  )
  val resources: List<Resource>
)
