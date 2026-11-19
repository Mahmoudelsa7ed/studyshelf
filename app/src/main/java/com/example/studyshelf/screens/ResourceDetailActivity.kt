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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.studyshelf.data.Resource
import com.example.studyshelf.data.StudyShelfDatabase
import com.example.studyshelf.ui.theme.StudyshelfTheme
import com.example.studyshelf.utils.IntentHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResourceDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val resourceId = intent.getIntExtra("EXTRA_RESOURCE_ID", 0)
        val subjectId = intent.getIntExtra("EXTRA_SUBJECT_ID", 0)
        val initialTitle = intent.getStringExtra("EXTRA_RESOURCE_TITLE") ?: "Lecture Resource"
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "Lectures"
        val initialUrl = intent.getStringExtra("EXTRA_RESOURCE_URL") ?: ""
        val initialDesc = intent.getStringExtra("EXTRA_RESOURCE_DESC") ?: ""

        val db = StudyShelfDatabase.getInstance(this)

        setContent {
            StudyshelfTheme {
                ResourceDetailScreen(
                    initialTitle = initialTitle,
                    category = category,
                    initialUrl = initialUrl,
                    initialDesc = initialDesc,
                    onUpdateResource = { updatedTitle, updatedUrl, updatedDesc ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.resourceDao().updateResource(
                                Resource(
                                    id = resourceId,
                                    subjectId = subjectId,
                                    title = updatedTitle,
                                    category = category,
                                    url = updatedUrl,
                                    description = updatedDesc
                                )
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@ResourceDetailActivity,
                                    "Resource updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    },
                    onDeleteResource = {
                        lifecycleScope.launch(Dispatchers.IO) {
                            db.resourceDao().deleteResource(
                                Resource(
                                    id = resourceId,
                                    subjectId = subjectId,
                                    title = initialTitle,
                                    category = category,
                                    url = initialUrl,
                                    description = initialDesc
                                )
                            )
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    this@ResourceDetailActivity,
                                    "Resource deleted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceDetailScreen(
    initialTitle: String,
    category: String,
    initialUrl: String,
    initialDesc: String,
    onUpdateResource: (String, String, String) -> Unit,
    onDeleteResource: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialTitle) }
    var url by remember { mutableStateOf(initialUrl) }
    var description by remember { mutableStateOf(initialDesc) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$category Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Section: $category",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Resource Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("Link / URL (YouTube, PDF, Drive)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes / Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            Button(
                onClick = { IntentHelper.openResourceUrl(context, url) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Open Link / Video in Browser")
            }

            OutlinedButton(
                onClick = { IntentHelper.shareResource(context, title, category, url, description) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Share Resource with Classmates")
            }

            OutlinedButton(
                onClick = { IntentHelper.sendEmailInquiry(context, title) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Email Inquiry")
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onUpdateResource(title.trim(), url.trim(), description.trim())
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("Update")
                }

                Button(
                    onClick = onDeleteResource,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Delete")
                }
            }
        }
    }
}