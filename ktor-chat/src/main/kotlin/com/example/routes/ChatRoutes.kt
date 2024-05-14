package com.example.routes

import com.example.room.MemberAlreadyExistsException
import com.example.room.RoomController
import com.example.sessions.ChatSession
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(
    roomController: RoomController
) {
    webSocket(path = "/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(
                reason = CloseReason(
                    code = CloseReason.Codes.VIOLATED_POLICY,
                    message = "Нет сеанса"
                )
            )
            return@webSocket
        }

        try {
            roomController.onJoin(
                username = session.username,
                sessionId = session.sessionId,
                socketSession = this
            )

            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUsername = session.username,
                        message = frame.readText()
                    )
                }
            }

        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(username = session.username)
        }
    }
}

fun Route.getAllMessages(
    roomController: RoomController
) {
    get(path = "/messages") {
        call.respond(
            status = HttpStatusCode.OK,
            message = roomController.getAllMessage()
        )
    }
}