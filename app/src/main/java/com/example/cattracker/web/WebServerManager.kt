package com.example.cattracker.web

object WebServerManager {
    private var server: CatWebServer? = null

    fun start(port: Int) {
        stop()
        server = CatWebServer(port).also { it.startServer() }
    }

    fun stop() {
        server?.stop()
        server = null
    }
}

