package com.example.ktor_chat_client.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsernameViewModel @Inject constructor(): ViewModel() {

    private val _state: MutableStateFlow<UsernameState> = MutableStateFlow(UsernameState())
    val state = _state.asStateFlow()

    private val _onJoinChat = MutableSharedFlow<String>()
    val onJoinChat = _onJoinChat.asSharedFlow()

    fun onAction(action: UsernameAction) {
        when(action) {
            UsernameAction.OnJoin -> {
                viewModelScope.launch {
                    if (_state.value.text.isNotBlank()) {
                        _onJoinChat.emit(_state.value.text)
                    }
                }
            }

            is UsernameAction.OnUsernameChange -> {
                _state.update {
                    it.copy(
                        text = action.username
                    )
                }
            }
        }
    }
}