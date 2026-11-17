package com.example.studyshelf.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshelf.R
import com.example.studyshelf.data.Resource
import com.example.studyshelf.data.StudyShelfDatabase
import com.example.studyshelf.data.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddResourceActivity : ComponentActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etUrl: EditText
    private lateinit var etDesc: EditText
    private lateinit var rbLectures: RadioButton
    private lateinit var rbLabs: RadioButton
    private lateinit var rbTasks: RadioButton
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resource)

        etTitle = findViewById(R.id.etResourceTitle)
        etUrl = findViewById(R.id.etResourceUrl)
        etDesc = findViewById(R.id.etResourceDesc)
        rbLectures = findViewById(R.id.rbLectures)
        rbLabs = findViewById(R.id.rbLabs)
        rbTasks = findViewById(R.id.rbTasks)
        btnSave = findViewById(R.id.btnSaveResource)
        btnBack = findViewById(R.id.btnBackAdd)

        var subjectId = intent.getIntExtra("EXTRA_SUBJECT_ID", 0)
        val passedCategory = intent.getStringExtra("EXTRA_CATEGORY") ?: "Lectures"

        when (passedCategory) {
            "Labs" -> rbLabs.isChecked = true
            "Tasks" -> rbTasks.isChecked = true
            else -> rbLectures.isChecked = true
        }

        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val receivedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (receivedText != null) {
                etUrl.setText(receivedText)
            }
        }

        if (savedInstanceState != null) {
            etTitle.setText(savedInstanceState.getString("SAVED_TITLE", ""))
            etUrl.setText(savedInstanceState.getString("SAVED_URL", ""))
            etDesc.setText(savedInstanceState.getString("SAVED_DESC", ""))
        }

        val db = StudyShelfDatabase.getInstance(this)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val url = etUrl.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val selectedCategory = when {
                rbLabs.isChecked -> "Labs"
                rbTasks.isChecked -> "Tasks"
                else -> "Lectures"
            }

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a resource title", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (subjectId == 0) {
                        val allSubjects = db.subjectDao().getAllSubjects()
                        subjectId = if (allSubjects.isNotEmpty()) {
                            allSubjects.first().id
                        } else {
                            db.subjectDao().insertSubject(
                                Subject(name = "General Study", description = "Shared resources")
                            ).toInt()
                        }
                    }

                    db.resourceDao().insertResource(
                        Resource(
                            subjectId = subjectId,
                            title = title,
                            category = selectedCategory,
                            url = url,
                            description = desc
                        )
                    )

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddResourceActivity, "Resource saved!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SAVED_TITLE", etTitle.text.toString())
        outState.putString("SAVED_URL", etUrl.text.toString())
        outState.putString("SAVED_DESC", etDesc.text.toString())
    }
}