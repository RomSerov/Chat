package com.example.ktor_chat_client.data.remote.dto

import com.example.ktor_chat_client.domain.model.Message
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.util.Date

@Serializable
data class MessageDto(
    val text: String,
    val time: Long,
    val username: String,
    val id: String
) {
    fun toMessage(): Message {
        val date = Date(time)
        val formatted = DateFormat.getDateInstance(DateFormat.DEFAULT).format(date)
        return Message(
            text = text,
            formattedTime = formatted,
            username = username
        )
    }
}
