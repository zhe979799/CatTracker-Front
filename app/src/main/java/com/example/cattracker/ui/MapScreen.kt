package com.example.cattracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.example.cattracker.data.ReportRepository

@Composable
fun MapScreen(repo: ReportRepository) {
    val mapView = rememberMapViewWithLifecycle()
    val reports by repo.reports.collectAsState(initial = emptyList())

    AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) { view ->
        val map = view.map
        map.uiSettings.isMyLocationButtonEnabled = true
        map.isMyLocationEnabled = true
        map.clear()
        for (r in reports) {
            val pos = LatLng(r.lat, r.lng)
            map.addMarker(MarkerOptions().position(pos).title(r.catId))
        }
        if (reports.isNotEmpty()) {
            val last = reports.last()
            map.moveCamera(
                CameraUpdateFactory.changeLatLng(LatLng(last.lat, last.lng))
            )
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context).apply { onCreate(null) } }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) { mapView.onResume() }
            override fun onPause(owner: LifecycleOwner) { mapView.onPause() }
            override fun onDestroy(owner: LifecycleOwner) { mapView.onDestroy() }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    return mapView
}
