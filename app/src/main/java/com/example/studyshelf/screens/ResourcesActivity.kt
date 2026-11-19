package com.example.studyshelf.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.studyshelf.R
import com.example.studyshelf.data.Resource
import com.example.studyshelf.data.StudyShelfDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResourcesActivity : ComponentActivity() {

    private lateinit var listView: ListView
    private lateinit var btnAdd: Button
    private lateinit var btnBack: Button
    private lateinit var tvHeader: TextView
    private lateinit var db: StudyShelfDatabase

    private var resources: MutableList<Resource> = mutableListOf()
    private var subjectId: Int = 0
    private var subjectName: String = "Subject"
    private var category: String = "Lectures"

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)

        listView = findViewById(R.id.listViewResources)
        btnAdd = findViewById(R.id.btnOpenAddResource)
        btnBack = findViewById(R.id.btnBack)
        tvHeader = findViewById(R.id.tvHeader)

        db = StudyShelfDatabase.getInstance(this)

        val bundle = intent.extras
        if (bundle != null) {
            subjectId = bundle.getInt("EXTRA_SUBJECT_ID", 0)
            subjectName = bundle.getString("EXTRA_SUBJECT_NAME", "Subject") ?: "Subject"
            category = bundle.getString("EXTRA_CATEGORY", "Lectures") ?: "Lectures"
        }

        tvHeader.text = "$subjectName - $category"

        loadResources()

        btnAdd.setOnClickListener {
            val intent = Intent(this, AddResourceActivity::class.java)
            intent.putExtra("EXTRA_SUBJECT_ID", subjectId)
            intent.putExtra("EXTRA_CATEGORY", category)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onRestart() {
        super.onRestart()
        loadResources()
    }

    private fun loadResources() {
        lifecycleScope.launch(Dispatchers.IO) {
            resources = db.resourceDao().getResourcesByCategory(subjectId, category).toMutableList()

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@ResourcesActivity,
                    android.R.layout.simple_list_item_1,
                    resources.map { "${it.title}\n${it.url}" }
                )
                listView.adapter = adapter

                listView.setOnItemClickListener { _, _, position, _ ->
                    val selectedResource = resources[position]
                    val detailIntent = Intent(this@ResourcesActivity, ResourceDetailActivity::class.java)
                    detailIntent.putExtra("EXTRA_RESOURCE_ID", selectedResource.id)
                    detailIntent.putExtra("EXTRA_SUBJECT_ID", selectedResource.subjectId)
                    detailIntent.putExtra("EXTRA_RESOURCE_TITLE", selectedResource.title)
                    detailIntent.putExtra("EXTRA_CATEGORY", selectedResource.category)
                    detailIntent.putExtra("EXTRA_RESOURCE_URL", selectedResource.url)
                    detailIntent.putExtra("EXTRA_RESOURCE_DESC", selectedResource.description)
                    startActivity(detailIntent)
                }
            }
        }
    }
}