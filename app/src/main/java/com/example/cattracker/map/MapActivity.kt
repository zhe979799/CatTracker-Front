package com.example.cattracker.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.PolylineOptions
import com.example.cattracker.R
import com.example.cattracker.data.ReportRepository
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                ReportRepository.reports.collect { reports ->
                    val map = mapView.map
                    map.clear()
                    val sorted = reports.sortedBy { it.timestamp }
                    val polyline = PolylineOptions()
                    sorted.forEach { report ->
                        val position = LatLng(report.lat, report.lng)
                        polyline.add(position)
                        val marker = MarkerOptions().position(position).title(report.catId)
                        report.info?.let { marker.snippet(it) }
                        map.addMarker(marker)
                    }
                    if (polyline.points.size > 1) {
                        map.addPolyline(polyline)
                    }
                    sorted.lastOrNull()?.let { last ->
                        val lastPos = LatLng(last.lat, last.lng)
                        map.moveCamera(CameraUpdateFactory.changeLatLng(lastPos))
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}
