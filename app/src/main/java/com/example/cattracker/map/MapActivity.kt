package com.example.cattracker.map

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.example.cattracker.R
import com.example.cattracker.data.ReportRepository

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        ReportRepository.reports.value.forEach { report ->
            addMarker(report.lat, report.lng, report.catId)
        }
    }

    private fun addMarker(lat: Double, lng: Double, title: String) {
        val map = mapView.map
        val position = LatLng(lat, lng)
        map.addMarker(MarkerOptions().position(position).title(title))
        map.moveCamera(CameraUpdateFactory.changeLatLng(position))
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
