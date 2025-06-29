package com.example.cattracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.CatWebServer
import com.example.cattracker.data.ReportRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val server = CatWebServer()
    private val repository = ReportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        server.startServer()
        setContent {
            CatTrackerTheme {
                AppContent(repository)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stopServer()
    }
}

@Composable
fun AppContent(repo: ReportRepository) {
    val scaffoldState = rememberScaffoldState()
    val reports by repo.reports.collectAsState()
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = { Text("Cat Tracker") })
        }
    ) { padding ->
        ReportList(reports)
    }
}

@Composable
fun ReportList(reports: List<com.example.cattracker.data.CatReport>) {
    androidx.compose.foundation.lazy.LazyColumn {
        items(reports.size) { index ->
            val r = reports[index]
            Text(text = "${'$'}{r.catId} @ ${'$'}{r.timestamp} (${r.lat},${r.lng})")
        }
    }
}

@Preview
@Composable
fun PreviewApp() {
    CatTrackerTheme {
        AppContent(ReportRepository)
    }
}
