package com.example.ktor_chat_client.data.remote

import com.example.ktor_chat_client.domain.model.Message

interface MessageService {

    suspend fun getAllMessage(): List<Message>

    companion object {
        const val BASE_URL = "http://10.0.2.2:8082"
    }

    sealed class Endpoints(val url: String) {
        data object AllMessage: Endpoints(url = "$BASE_URL/messages")
    }
}