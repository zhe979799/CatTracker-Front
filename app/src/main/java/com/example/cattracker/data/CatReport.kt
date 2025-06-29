package com.example.cattracker.data

import org.json.JSONObject

/**
 * Data format for cat reporting
 */
data class CatReport(
    val catId: String,
    val timestamp: Long,
    val lat: Double,
    val lng: Double,
    val info: String? = null
) {
    fun toJson(): JSONObject = JSONObject().apply {
        put("catId", catId)
        put("timestamp", timestamp)
        put("lat", lat)
        put("lng", lng)
        put("info", info)
    }

    companion object {
        fun fromJson(obj: JSONObject): CatReport = CatReport(
            catId = obj.getString("catId"),
            timestamp = obj.getLong("timestamp"),
            lat = obj.getDouble("lat"),
            lng = obj.getDouble("lng"),
            info = obj.optString("info")
        )
    }
}
