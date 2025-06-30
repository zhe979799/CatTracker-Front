package com.example.cattracker.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.example.cattracker.data.CatReport
import com.example.cattracker.data.ReportRepository
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BluetoothService(
    private val address: String,
    private val onResult: ((Boolean) -> Unit)? = null
) {
    private val adapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var socket: BluetoothSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("MissingPermission")
    fun connect() {
        adapter ?: return
        val device: BluetoothDevice? = adapter.bondedDevices.firstOrNull { it.address == address }
        device?.let {
            val uuid = it.uuids?.firstOrNull()?.uuid ?: UUID.randomUUID()
            socket = it.createRfcommSocketToServiceRecord(uuid)
            scope.launch {
                try {
                    socket?.connect()
                    onResult?.invoke(true)
                    listen()
                } catch (e: Exception) {
                    Log.e("BluetoothService", "connection error", e)
                    onResult?.invoke(false)
                }
            }
        }
    }

    private fun listen() {
        val reader = BufferedReader(InputStreamReader(socket?.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let {
                try {
                    val obj = JSONObject(it)
                    val report = CatReport.fromJson(obj)
                    ReportRepository.addReport(report)
                } catch (e: Exception) {
                    Log.e("BluetoothService", "invalid data", e)
                }
            }
        }
    }
}
