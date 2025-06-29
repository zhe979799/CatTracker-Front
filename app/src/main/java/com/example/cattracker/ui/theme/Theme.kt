package com.example.cattracker.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

@Composable
fun CatTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colors = darkColors()) {
        content()
    }
}
