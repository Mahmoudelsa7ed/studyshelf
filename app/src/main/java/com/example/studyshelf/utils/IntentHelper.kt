package com.example.studyshelf.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri

object IntentHelper {

    fun openResourceUrl(context: Context, rawUrl: String) {
        if (rawUrl.isBlank()) {
            Toast.makeText(context, "No link attached to this resource", Toast.LENGTH_SHORT).show()
            return
        }
        val formattedUrl = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else {
            rawUrl
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW, formattedUrl.toUri())
            context.startActivity(intent)
        } catch (_: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareResource(context: Context, title: String, category: String, url: String, description: String) {
        val shareText = buildString {
            append("StudyShelf - $category\n")
            append("Title: $title\n")
            if (description.isNotBlank()) append("Notes: $description\n")
            if (url.isNotBlank()) append("Link: $url")
        }
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        val chooser = Intent.createChooser(sendIntent, "Share Study Resource via")
        context.startActivity(chooser)
    }

    fun sendEmailInquiry(context: Context, subjectTitle: String) = try {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:".toUri()).apply {
            putExtra(Intent.EXTRA_SUBJECT, "StudyShelf Question: $subjectTitle")
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
    }
}