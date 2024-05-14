package com.example.ktor_chat_client.presentation.screens.chat

sealed class ChatAction {
    data class OnMessageChange(val text: String): ChatAction()
    data object OnSendMessage: ChatAction()
    data object Connect: ChatAction()
    data object Disconnect: ChatAction()
}

sealed class ChatEvent {
    data object ToastError: ChatEvent()
}