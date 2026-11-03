package com.example.studyshelf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.studyshelf.data.StudyShelfDatabase
import com.example.studyshelf.data.Subject
import com.example.studyshelf.screens.AddEditSubjectActivity
import com.example.studyshelf.screens.SubjectDetailsActivity
import com.example.studyshelf.ui.theme.StudyshelfTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val subjectsState = mutableStateListOf<Subject>()
    private var searchQueryState by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("StudyShelfPrefs", Context.MODE_PRIVATE)
        val savedStudent = sharedPref.getString("student_info", "Student Name - ID") ?: "Student Name - ID"

        loadSubjects("")

        setContent {
            StudyshelfTheme {
                HomeScreen(
                    initialStudentInfo = savedStudent,
                    subjects = subjectsState,
                    searchQuery = searchQueryState,
                    onSearchChange = { query ->
                        searchQueryState = query
                        loadSubjects(query)
                    },
                    onSaveStudentInfo = { newInfo ->
                        sharedPref.edit().putString("student_info", newInfo).apply()
                        Toast.makeText(this, "Student info saved!", Toast.LENGTH_SHORT).show()
                    },
                    onAddSubjectClick = {
                        val intent = Intent(this, AddEditSubjectActivity::class.java)
                        startActivity(intent)
                    },
                    onEditSubjectClick = { subject ->
                        val intent = Intent(this, AddEditSubjectActivity::class.java).apply {
                            putExtra("EXTRA_SUBJECT_ID", subject.id)
                            putExtra("EXTRA_SUBJECT_NAME", subject.name)
                            putExtra("EXTRA_SUBJECT_DESC", subject.description)
                        }
                        startActivity(intent)
                    },
                    onSubjectClick = { subject ->
                        val intent = Intent(this, SubjectDetailsActivity::class.java).apply {
                            putExtra("EXTRA_SUBJECT_ID", subject.id)
                            putExtra("EXTRA_SUBJECT_NAME", subject.name)
                            putExtra("EXTRA_SUBJECT_DESC", subject.description)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadSubjects(searchQueryState)
    }

    private fun loadSubjects(query: String) {
        val db = StudyShelfDatabase.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val list = if (query.isBlank()) {
                db.subjectDao().getAllSubjects()
            } else {
                db.subjectDao().searchSubjects(query)
            }
            withContext(Dispatchers.Main) {
                subjectsState.clear()
                subjectsState.addAll(list)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    initialStudentInfo: String,
    subjects: List<Subject>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSaveStudentInfo: (String) -> Unit,
    onAddSubjectClick: () -> Unit,
    onEditSubjectClick: (Subject) -> Unit,
    onSubjectClick: (Subject) -> Unit
) {
    var studentInfo by remember { mutableStateOf(initialStudentInfo) }
    var isEditingStudent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("StudyShelf", fontWeight = FontWeight.Bold)
                        Text(
                            text = studentInfo,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE8F5E9)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { isEditingStudent = !isEditingStudent }) {
                        Text(if (isEditingStudent) "Close" else "Profile", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSubjectClick,
                containerColor = Color(0xFF2E7D32),
                contentColor = Color.White
            ) {
                Text("+ Add", modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isEditingStudent) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = studentInfo,
                            onValueChange = { studentInfo = it },
                            label = { Text("Student Name & ID") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onSaveStudentInfo(studentInfo)
                                isEditingStudent = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                label = { Text("Search subjects...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "My Subjects (${subjects.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (subjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No subjects yet. Tap '+ Add' to add your first college subject!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(subjects) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSubjectClick(subject) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(Color(0xFF2E7D32), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = subject.name.take(1).uppercase(),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = subject.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (subject.description.isNotBlank()) {
                                            Text(
                                                text = subject.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.DarkGray
                                            )
                                        }
                                    }
                                }
                                TextButton(onClick = { onEditSubjectClick(subject) }) {
                                    Text("Edit", color = Color(0xFF2E7D32))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}