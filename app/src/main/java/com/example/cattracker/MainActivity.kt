package com.example.cattracker

import android.Manifest
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import androidx.compose.material.TextButton
import com.example.cattracker.data.ReportRepository
import com.example.cattracker.ui.MapScreen
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager
import com.example.cattracker.AppPrefs
import com.example.cattracker.SettingsActivity
import com.example.cattracker.ReportListActivity
import com.amap.api.maps.MapsInitializer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

        ReportRepository.init(applicationContext)
        // 启动服务器
        WebServerManager.start(AppPrefs.port)

        MapsInitializer.updatePrivacyShow(applicationContext, true, true)
        MapsInitializer.updatePrivacyAgree(applicationContext, true)

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
                    TextButton(onClick = {
                        context.startActivity(Intent(context, ReportListActivity::class.java))
                    }) {
                        Text("Reports", color = MaterialTheme.colors.onPrimary)
                    }
                }
            )
        }
    ) { padding ->
        MapScreen(repo)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewApp() {
    CatTrackerTheme {
        AppContent(ReportRepository)
    }
}
