package com.example.cattracker.web

object WebServerManager {
    private var server: CatWebServer? = null

    fun start(port: Int): Boolean {
        stop()
        val srv = CatWebServer(port)
        return if (srv.startServer()) {
            server = srv
            true
        } else {
            false
        }
    }

    fun stop() {
        server?.stop()
        server = null
    }
}

