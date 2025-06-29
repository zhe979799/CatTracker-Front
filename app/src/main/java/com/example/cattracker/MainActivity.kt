package com.example.cattracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import androidx.compose.material.TextButton
import com.example.cattracker.data.CatReport
import com.example.cattracker.data.ReportRepository
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager
import com.example.cattracker.AppPrefs
import com.example.cattracker.SettingsActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReportRepository.init(applicationContext)
        // 启动服务器
        WebServerManager.start(AppPrefs.port)

        setContent {
            CatTrackerTheme {
                AppContent(ReportRepository)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭服务器，释放端口
        WebServerManager.stop()
    }
}

@Composable
fun AppContent(repo: ReportRepository) {
    val scaffoldState = rememberScaffoldState()
    val reports by repo.reports.collectAsState(initial = emptyList())

    val context = LocalContext.current
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Cat Tracker") },
                actions = {
                    TextButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Text("Settings", color = MaterialTheme.colors.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        ReportList(
            reports = reports,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun ReportList(reports: List<CatReport>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(reports) { r ->
            Text(text = "${r.catId} @ ${r.timestamp} (${r.lat}, ${r.lng})")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    CatTrackerTheme {
        AppContent(ReportRepository)
    }
}
