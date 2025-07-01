package com.example.cattracker.data

import android.content.Context
import com.example.cattracker.AppPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


object ReportRepository {
    private val _reports = MutableStateFlow<List<CatReport>>(emptyList())
    val reports = _reports.asStateFlow()
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun init(context: Context) {
        AppPrefs.init(context)
        _reports.value = AppPrefs.loadReports()
    }

    fun addReport(report: CatReport) {
        _reports.value = _reports.value + report
        ioScope.launch { AppPrefs.saveReports(_reports.value) }
    }

    /**
     * Cancel the background coroutine scope used for saving reports.
     * Call from the app's cleanup logic to avoid leaking coroutines.
     */
    fun close() {
        ioScope.cancel()
    }
}
