package com.example.cattracker.data

import android.content.Context
import com.example.cattracker.AppPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object ReportRepository {
    private val _reports = MutableStateFlow<List<CatReport>>(emptyList())
    val reports = _reports.asStateFlow()

    fun init(context: Context) {
        AppPrefs.init(context)
        _reports.value = AppPrefs.loadReports()
    }

    fun addReport(report: CatReport) {
        _reports.value = _reports.value + report
        AppPrefs.saveReports(_reports.value)
    }
}
