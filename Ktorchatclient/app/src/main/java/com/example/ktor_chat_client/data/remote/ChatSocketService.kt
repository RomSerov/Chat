package com.example.ktor_chat_client.data.remote

import com.example.ktor_chat_client.domain.model.Message
import com.example.ktor_chat_client.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(username: String): Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        const val BASE_URL = "ws://10.0.2.2:8082"
    }

    sealed class Endpoints(val url: String) {
        data object ChatSocket: Endpoints(url = "$BASE_URL/chat-socket")
    }
}