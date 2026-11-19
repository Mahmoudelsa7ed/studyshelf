package com.example.studyshelf.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.studyshelf.data.StudyShelfDatabase
import com.example.studyshelf.data.Subject
import com.example.studyshelf.ui.theme.StudyshelfTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEditSubjectActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subjectId = intent.getIntExtra("EXTRA_SUBJECT_ID", 0)
        val initialName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: ""
        val initialDesc = intent.getStringExtra("EXTRA_SUBJECT_DESC") ?: ""
        val isEditMode = subjectId != 0

        val db = StudyShelfDatabase.getInstance(this)

        setContent {
            StudyshelfTheme {
                AddEditSubjectScreen(
                    isEditMode = isEditMode,
                    initialName = initialName,
                    initialDesc = initialDesc,
                    onSaveClick = { name, description ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            if (isEditMode) {
                                db.subjectDao().updateSubject(
                                    Subject(
                                        id = subjectId,
                                        name = name,
                                        description = description
                                    )
                                )
                            } else {
                                db.subjectDao().insertSubject(
                                    Subject(
                                        name = name,
                                        description = description
                                    )
                                )
                            }

                            withContext(Dispatchers.Main) {
                                val msg = if (isEditMode) "Subject updated!" else "Subject saved!"
                                Toast.makeText(
                                    this@AddEditSubjectActivity,
                                    msg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    },
                    onDeleteClick = {
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.subjectDao().deleteSubject(
                                Subject(
                                    id = subjectId,
                                    name = initialName,
                                    description = initialDesc
                                )
                            )

                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@AddEditSubjectActivity,
                                    "Subject deleted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    },
                    onBackClick = {
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubjectScreen(
    isEditMode: Boolean,
    initialName: String,
    initialDesc: String,
    onSaveClick: (String, String) -> Unit,
    onDeleteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    var name by remember { mutableStateOf(initialName) }
    var description by remember { mutableStateOf(initialDesc) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Subject" else "Add Subject",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("← Back", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Subject Name * (e.g. Algorithms)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    if (name.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please enter subject name",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        onSaveClick(name.trim(), description.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32)
                )
            ) {
                Text(if (isEditMode) "Update Subject" else "Save Subject")
            }

            if (isEditMode) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFC62828)
                    )
                ) {
                    Text("Delete Subject")
                }
            }
        }
    }
}