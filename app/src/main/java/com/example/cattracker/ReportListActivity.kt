package com.example.cattracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cattracker.data.ReportRepository
import com.example.cattracker.ui.ReportListScreen
import com.example.cattracker.ui.theme.CatTrackerTheme

class ReportListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatTrackerTheme {
                ReportListScreen(ReportRepository)
            }
        }
    }
}
