package com.example.ktor_chat_client.data.remote

import com.example.ktor_chat_client.data.remote.dto.MessageDto
import com.example.ktor_chat_client.domain.model.Message
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class MessageServiceImpl(
    private val client: HttpClient
) : MessageService {

    override suspend fun getAllMessage(): List<Message> {
        return try {
            client.get<List<MessageDto>>(MessageService.Endpoints.AllMessage.url)
                .map {
                    it.toMessage()
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}