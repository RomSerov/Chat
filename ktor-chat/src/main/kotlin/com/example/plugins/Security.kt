package com.example.plugins

import com.example.sessions.ChatSession
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<ChatSession>() == null) {
            val username = call.parameters["username"] ?: "Гость"
            call.sessions.set(ChatSession(username, generateNonce()))
        }
    }
}
