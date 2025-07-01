package com.example.cattracker.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.example.cattracker.R
import com.example.cattracker.data.ReportRepository
import java.util.Calendar

@Composable
fun MapScreen(repo: ReportRepository) {
    val mapView = rememberMapViewWithLifecycle()
    // Map object must be re-acquired if the MapView instance changes
    val map = remember(mapView) { mapView.map }
    val reports by repo.reports.collectAsState(initial = emptyList())
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val markers = remember { mutableMapOf<String, Marker>() }
    var polyline by remember { mutableStateOf<Polyline?>(null) }

    if (showDatePicker) {
        LaunchedEffect(Unit) {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, y, m, d ->
                    val sel = Calendar.getInstance().apply {
                        set(y, m, d, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    selectedDate = sel.timeInMillis
                    showDatePicker = false
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).apply {
                // Hide the picker when the user cancels without selecting a date
                setOnCancelListener { showDatePicker = false }
            }.show()
        }
    }

    LaunchedEffect(map) {
        map.uiSettings.isMyLocationButtonEnabled = true
        map.uiSettings.isZoomControlsEnabled = true
        map.isMyLocationEnabled = true
    }

    LaunchedEffect(reports, selectedDate) {
        val filtered = selectedDate?.let { date ->
            val end = date + 24 * 60 * 60 * 1000
            reports.filter { it.timestamp in date until end }
        } ?: reports

        val ids = filtered.map { "${'$'}{it.catId}_${'$'}{it.timestamp}" }.toSet()
        val removeIds = markers.keys - ids
        removeIds.forEach { id ->
            markers.remove(id)?.remove()
        }

        val sortedReports = filtered.sortedBy { it.timestamp }
        for (r in sortedReports) {
            val id = "${'$'}{r.catId}_${'$'}{r.timestamp}"
            val pos = LatLng(r.lat, r.lng)
            val m = markers[id]
            if (m == null) {
                val opts = MarkerOptions().position(pos).title(r.catId)
                r.info?.let { opts.snippet(it) }
                markers[id] = map.addMarker(opts)
            } else {
                m.position = pos
                m.title = r.catId
                m.snippet = r.info
            }
        }

        val points = sortedReports.map { LatLng(it.lat, it.lng) }
        if (points.size > 1) {
            if (polyline == null) {
                polyline = map.addPolyline(PolylineOptions().addAll(points))
            } else {
                polyline!!.points = points
            }
        } else {
            polyline?.remove()
            polyline = null
        }

        sortedReports.lastOrNull()?.let { last ->
            map.moveCamera(
                CameraUpdateFactory.changeLatLng(LatLng(last.lat, last.lng))
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(onClick = {
                map.animateCamera(CameraUpdateFactory.zoomIn())
            }) { Icon(painter = painterResource(R.drawable.baseline_zoom_in_24),
                contentDescription = "Zoom in",
                modifier = Modifier.size(24.dp)) }
            FloatingActionButton(onClick = {
                map.animateCamera(CameraUpdateFactory.zoomOut())
            }) { Icon(painter = painterResource(R.drawable.zoom_out_24),
                contentDescription = "Zoom out",
                modifier = Modifier.size(24.dp)) }
            FloatingActionButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
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
