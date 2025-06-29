package com.example.cattracker

import android.content.Context
import android.content.SharedPreferences
import com.example.cattracker.data.CatReport
import org.json.JSONArray

object AppPrefs {
    private const val PREFS_NAME = "cattracker_prefs"
    private const val KEY_PORT = "server_port"
    private const val KEY_REPORTS = "reports"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var port: Int
        get() = prefs.getInt(KEY_PORT, 8080)
        set(value) { prefs.edit().putInt(KEY_PORT, value).apply() }

    fun loadReports(): List<CatReport> {
        val json = prefs.getString(KEY_REPORTS, null) ?: return emptyList()
        val arr = JSONArray(json)
        val list = mutableListOf<CatReport>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            list.add(CatReport.fromJson(obj))
        }
        return list
    }

    fun saveReports(reports: List<CatReport>) {
        val arr = JSONArray()
        for (r in reports) {
            arr.put(r.toJson())
        }
        prefs.edit().putString(KEY_REPORTS, arr.toString()).apply()
    }
}

