package com.example.cattracker

import android.Manifest
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import android.content.Intent
import androidx.compose.material.IconButton
import androidx.compose.material.TextButton
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.LaunchedEffect
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.Settings
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.cattracker.data.ReportRepository
import com.example.cattracker.ui.MapScreen
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager
import com.example.cattracker.AppPrefs
import com.example.cattracker.SettingsActivity
import com.example.cattracker.ReportListActivity
import com.example.cattracker.R
import com.amap.api.maps.MapsInitializer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.List

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        // Stop the embedded HTTP server and close the repository's coroutine
        // scope to avoid leaking background work
        WebServerManager.stop()
        ReportRepository.close()
    }
}

@Composable
fun AppContent(repo: ReportRepository) {
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current
    var showPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (!granted) showPermission = true }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showPermission) {
        AlertDialog(
            onDismissRequest = {},
            text = { Text(stringResource(R.string.msg_location_permission)) },
            confirmButton = {
                TextButton(onClick = {
                    showPermission = false
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) { Text(stringResource(R.string.action_grant_permission)) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPermission = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) { Text(stringResource(R.string.action_open_settings)) }
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_main)) },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.btn_settings),
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                    IconButton(onClick = {
                        context.startActivity(Intent(context, ReportListActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = stringResource(R.string.btn_reports),
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            )
        }
    ) { _ ->
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
