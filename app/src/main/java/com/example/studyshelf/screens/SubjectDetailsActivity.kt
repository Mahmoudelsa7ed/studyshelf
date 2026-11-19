package com.example.studyshelf.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.studyshelf.ui.theme.StudyshelfTheme

class SubjectDetailsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subjectId = intent.getIntExtra("EXTRA_SUBJECT_ID", 0)
        val subjectName = intent.getStringExtra("EXTRA_SUBJECT_NAME") ?: "Subject"
        val subjectDesc = intent.getStringExtra("EXTRA_SUBJECT_DESC") ?: ""

        setContent {
            StudyshelfTheme {
                SubjectSectionsScreen(
                    subjectName = subjectName,
                    subjectDesc = subjectDesc,
                    onSectionClick = { category ->
                        val bundle = Bundle().apply {
                            putInt("EXTRA_SUBJECT_ID", subjectId)
                            putString("EXTRA_SUBJECT_NAME", subjectName)
                            putString("EXTRA_CATEGORY", category)
                        }

                        val intent = Intent(
                            this,
                            ResourcesActivity::class.java
                        )
                        intent.putExtras(bundle)
                        startActivity(intent)
                    },
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSectionsScreen(
    subjectName: String,
    subjectDesc: String,
    onSectionClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val sections = listOf(
        Triple(
            "Lectures",
            "Lecture videos, slides links, and lecture PDFs",
            Color(0xFFE8F5E9)
        ),
        Triple(
            "Labs",
            "Lab explanations, practical files, and tutorials",
            Color(0xFFE3F2FD)
        ),
        Triple(
            "Tasks",
            "Assignments, project briefs, and past exams",
            Color(0xFFFFF3E0)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        subjectName,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = Color.White)
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (subjectDesc.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(
                        text = subjectDesc,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }

            Text(
                text = "Subject Sections (Tap to open):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            sections.forEach { (categoryName, subtitle, bgColor) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSectionClick(categoryName)
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = bgColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}