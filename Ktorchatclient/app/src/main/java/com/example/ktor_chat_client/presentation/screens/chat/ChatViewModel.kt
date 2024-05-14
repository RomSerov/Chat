package com.example.ktor_chat_client.presentation.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktor_chat_client.data.remote.ChatSocketService
import com.example.ktor_chat_client.data.remote.MessageService
import com.example.ktor_chat_client.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state: MutableStateFlow<ChatState> = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<ChatEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnMessageChange -> {
                _state.update {
                    it.copy(
                        textMessage = action.text
                    )
                }
            }

            ChatAction.Disconnect -> {
                viewModelScope.launch {
                    chatSocketService.closeSession()
                }
            }

            ChatAction.OnSendMessage -> {
                viewModelScope.launch {
                    if (state.value.textMessage.isNotBlank()) {
                        chatSocketService.sendMessage(message = state.value.textMessage)
                    }
                }
            }

            ChatAction.Connect -> {
                connect()
            }
        }

    }

    private fun connect() {
        getAllMessage()
        savedStateHandle.get<String>("username")?.let {
            viewModelScope.launch {
                when (chatSocketService.initSession(it)) {
                    is Resource.Error -> {
                        _uiEvent.send(ChatEvent.ToastError)
                    }

                    is Resource.Success -> {
                        chatSocketService.observeMessages()
                            .onEach {
                                val newList = state.value.messages
                                    .toMutableList()
                                    .apply {
                                        add(0, it)
                                    }
                                _state.update {
                                    it.copy(
                                        messages = newList
                                    )
                                }
                            }
                            .launchIn(viewModelScope)
                    }
                }
            }
        }
    }

    private fun getAllMessage() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val response = messageService.getAllMessage()
            _state.update {
                it.copy(
                    isLoading = false,
                    messages = response
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        onAction(ChatAction.Disconnect)
    }
}