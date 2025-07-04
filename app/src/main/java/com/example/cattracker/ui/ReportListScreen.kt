package com.example.cattracker.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.cattracker.R
import com.example.cattracker.data.ReportRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportListScreen(repo: ReportRepository) {
    val reports by repo.reports.collectAsState(initial = emptyList())
    val sorted = reports.sortedByDescending { it.timestamp }
    if (sorted.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.msg_no_reports))
        }
    } else {
        LazyColumn {
            items(sorted) { r ->
                val time = dateFormat.format(Date(r.timestamp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = "${r.catId} - $time (${r.lat}, ${r.lng})")
                        r.info?.let { Text(it) }
                    }
                }
                Divider()
            }
        }
    }
}

private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
