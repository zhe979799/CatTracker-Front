package com.example.cattracker.web

import android.util.Log
import com.example.cattracker.data.CatReport
import com.example.cattracker.data.ReportRepository
import fi.iki.elonen.NanoHTTPD
import org.json.JSONObject

class CatWebServer(port: Int) : NanoHTTPD(port) {

    fun startServer(): Boolean {
        return try {
            start(SOCKET_READ_TIMEOUT, false)
            Log.i("CatWebServer", "Server started on port $listeningPort")
            true
        } catch (e: java.io.IOException) {
            Log.e("CatWebServer", "start failed", e)
            false
        }
    }

    override fun serve(session: IHTTPSession): Response {
        val map = HashMap<String, String>()
        session.parseBody(map)
        val json = session.parms["data"] ?: map["postData"]
        json?.let {
            try {
                val obj = JSONObject(it)
                val report = CatReport.fromJson(obj)
                ReportRepository.addReport(report)
            } catch (e: Exception) {
                Log.e("CatWebServer", "Invalid json", e)
            }
        }
        return newFixedLengthResponse("ok")
    }
}
