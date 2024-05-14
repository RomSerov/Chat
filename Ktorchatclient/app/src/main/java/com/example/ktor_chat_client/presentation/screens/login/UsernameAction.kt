package com.example.ktor_chat_client.presentation.screens.login

sealed class UsernameAction {
    data object OnJoin: UsernameAction()
    data class OnUsernameChange(val username: String): UsernameAction()
}