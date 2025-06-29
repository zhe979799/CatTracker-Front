package com.example.cattracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatTrackerTheme {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    var portText by remember { mutableStateOf(AppPrefs.port.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = portText,
            onValueChange = { portText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Server Port") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                portText.toIntOrNull()?.let { port ->
                    AppPrefs.port = port
                    WebServerManager.start(port)
                }
            }) { Text("Start Server") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { WebServerManager.stop() }) { Text("Stop Server") }
        }
    }
}

