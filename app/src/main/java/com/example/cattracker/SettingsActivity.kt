package com.example.cattracker

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.widget.Toast
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager
import com.example.cattracker.bluetooth.BluetoothService

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
    var isPortError by remember { mutableStateOf(false) }
    var addressText by remember { mutableStateOf(AppPrefs.bluetoothAddress) }
    val context = LocalContext.current

    val btPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            BluetoothService(AppPrefs.bluetoothAddress).connect()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = portText,
            onValueChange = {
                portText = it.filter { ch -> ch.isDigit() }
                isPortError = portText.toIntOrNull() == null
            },
            label = { Text("Server Port") },
            isError = isPortError
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = addressText,
            onValueChange = {
                addressText = it
                AppPrefs.bluetoothAddress = it
            },
            label = { Text("Bluetooth MAC Address") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                val port = portText.toIntOrNull()
                if (port != null) {
                    isPortError = false
                    AppPrefs.port = port
                    WebServerManager.start(port)
                } else {
                    isPortError = true
                    Toast.makeText(context, "Invalid server port", Toast.LENGTH_SHORT).show()
                }
            }) { Text("Start Server") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { WebServerManager.stop() }) { Text("Stop Server") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        BluetoothService(AppPrefs.bluetoothAddress).connect()
                    } else {
                        btPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                } else {
                    BluetoothService(AppPrefs.bluetoothAddress).connect()
                }
            }) { Text("Connect BT") }
        }
    }
}

