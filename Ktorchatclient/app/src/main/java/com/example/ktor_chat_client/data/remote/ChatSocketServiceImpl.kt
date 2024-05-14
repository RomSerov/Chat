package com.example.ktor_chat_client.data.remote

import com.example.ktor_chat_client.data.remote.dto.MessageDto
import com.example.ktor_chat_client.domain.model.Message
import com.example.ktor_chat_client.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else {
                Resource.Error(message = "Не удалось установить соединение...")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(message = e.localizedMessage ?: "Неизвестная ошибка")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(frame = Frame.Text(text = message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map { frame ->
                    val json = (frame as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {  }
        } catch (e: Exception) {
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }
}