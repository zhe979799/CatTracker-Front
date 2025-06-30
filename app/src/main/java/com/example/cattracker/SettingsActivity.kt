package com.example.cattracker

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cattracker.bluetooth.BluetoothService
import com.example.cattracker.ui.theme.CatTrackerTheme
import com.example.cattracker.web.WebServerManager
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    val adapter = BluetoothAdapter.getDefaultAdapter()
    val devices = remember { mutableStateListOf<BluetoothDevice>() }

    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        if (devices.none { d -> d.address == it.address }) {
                            devices.add(it)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(receiver, filter)
        adapter?.bondedDevices?.let { devices.addAll(it) }
        adapter?.startDiscovery()
        onDispose {
            context.unregisterReceiver(receiver)
            adapter?.cancelDiscovery()
        }
    }

    val btPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            BluetoothService(AppPrefs.bluetoothAddress) { success ->
                Toast.makeText(
                    context,
                    if (success) context.getString(R.string.msg_bt_connect_success)
                    else context.getString(R.string.msg_bt_connect_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }.connect()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = portText,
            onValueChange = {
                portText = it.filter { ch -> ch.isDigit() }
                isPortError = portText.toIntOrNull() == null
            },
            label = { Text(stringResource(R.string.label_server_port)) },
            isError = isPortError
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = addressText,
            onValueChange = {
                addressText = it
                AppPrefs.bluetoothAddress = it
            },
            label = { Text(stringResource(R.string.label_bt_address)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        var expanded by remember { mutableStateOf(false) }
        Button(onClick = { expanded = true }) {
            Text(stringResource(R.string.btn_select_device))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            devices.forEach { device ->
                DropdownMenuItem(onClick = {
                    addressText = device.address
                    AppPrefs.bluetoothAddress = device.address
                    expanded = false
                }) { Text(device.name ?: device.address) }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                val port = portText.toIntOrNull()
                if (port != null && port in 1..65535) {
                    isPortError = false
                    AppPrefs.port = port
                    val ip = getLocalIp(context)
                    val started = WebServerManager.start(port)
                    Toast.makeText(
                        context,
                        if (started) {
                            context.getString(
                                R.string.msg_server_started,
                                "http://$ip:$port"
                            )
                        } else {
                            context.getString(R.string.msg_server_failed)
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    isPortError = true
                    Toast.makeText(
                        context,
                        context.getString(R.string.msg_invalid_port),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) { Text(stringResource(R.string.btn_start_server)) }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                WebServerManager.stop()
            }) { Text(stringResource(R.string.btn_stop_server)) }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {
                        BluetoothService(AppPrefs.bluetoothAddress) { success ->
                            Toast.makeText(
                                context,
                                if (success) context.getString(R.string.msg_bt_connect_success)
                                else context.getString(R.string.msg_bt_connect_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }.connect()
                    } else {
                        btPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    }
                } else {
                    BluetoothService(AppPrefs.bluetoothAddress) { success ->
                        Toast.makeText(
                            context,
                            if (success) context.getString(R.string.msg_bt_connect_success)
                            else context.getString(R.string.msg_bt_connect_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }.connect()
                }
            }) { Text(stringResource(R.string.btn_connect_bt)) }
        }
    }
}

private fun getLocalIp(context: Context): String? {
    val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    val ipInt = wm?.connectionInfo?.ipAddress ?: return null
    val bytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()
    return InetAddress.getByAddress(bytes).hostAddress
}
