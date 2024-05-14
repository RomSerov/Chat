package com.example.ktor_chat_client.presentation.screens.chat

import com.example.ktor_chat_client.domain.model.Message

data class ChatState(
    val textMessage: String = "",
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList()
)
