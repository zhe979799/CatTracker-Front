package com.example.cattracker.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ReportRepository {
    private val _reports = MutableStateFlow<List<CatReport>>(emptyList())
    val reports = _reports.asStateFlow()

    fun addReport(report: CatReport) {
        _reports.value = _reports.value + report
    }
}
