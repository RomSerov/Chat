package com.example

import com.example.di.mainModule
import com.example.plugins.*
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(Koin) {
        modules(modules = mainModule)
    }
    configureSockets()
    configureRouting()
    configureSerialization()
    configureMonitoring()
    configureSecurity()
}
