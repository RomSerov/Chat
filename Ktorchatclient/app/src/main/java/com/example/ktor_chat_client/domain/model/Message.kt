package com.example.ktor_chat_client.domain.model

data class Message(
    val text: String,
    val formattedTime: String,
    val username: String
)
