package com.example.cattracker.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.cattracker.data.ReportRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportListScreen(repo: ReportRepository) {
    val reports by repo.reports.collectAsState(initial = emptyList())
    val sorted = reports.sortedBy { it.timestamp }
    LazyColumn {
        items(sorted) { r ->
            val time = dateFormat.format(Date(r.timestamp))
            Text(
                text = "${r.catId} - $time (${r.lat}, ${r.lng})",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
