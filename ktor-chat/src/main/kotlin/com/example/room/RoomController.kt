package com.example.room

import com.example.data.MessageDataSource
import com.example.data.model.Message
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        username: String,
        sessionId: String,
        socketSession: WebSocketSession
    ) {
        if (members.containsKey(username)) throw MemberAlreadyExistsException()

        members[username] = Member(
            username = username,
            sessionId = sessionId,
            socket = socketSession
        )
    }

    suspend fun sendMessage(
        senderUsername: String,
        message: String
    ) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                username = senderUsername,
                time = System.currentTimeMillis()
            )
            messageDataSource.insertMessage(message = messageEntity)

            val parseMessage = Json.encodeToString(messageEntity)
            member.socket.send(frame = Frame.Text(text = parseMessage))
        }
    }

    suspend fun getAllMessage(): List<Message> {
        return messageDataSource.getAllMessage()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}