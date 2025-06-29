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
import androidx.compose.ui.tooling.preview.Preview
import com.example.cattracker.data.CatReport
import com.example.cattracker.data.ReportRepository
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.CatWebServer

class MainActivity : ComponentActivity() {

    /** 本地 HTTP/WebSocket 服务器 */
    private val server = CatWebServer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 启动服务器
        server.start()

        setContent {
            CatTrackerTheme {
                AppContent(ReportRepository)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭服务器，释放端口
        server.stop()
    }
}

@Composable
fun AppContent(repo: ReportRepository) {
    val scaffoldState = rememberScaffoldState()
    val reports by repo.reports.collectAsState(initial = emptyList())

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopAppBar(title = { Text("Cat Tracker") }) }
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
