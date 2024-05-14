package com.example.ktor_chat_client.presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ktor_chat_client.presentation.ui.theme.KtorchatclientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun UsernameScreen(
    viewModel: UsernameViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.onJoinChat.collectLatest { username ->
            onNavigate("chat_screen/$username")
        }
    }

    UsernameScreenRender(
        state = state,
        onValueChange = {
            viewModel.onAction(UsernameAction.OnUsernameChange(it))
        },
        onClick = {
            viewModel.onAction(UsernameAction.OnJoin)
        }
    )
}

@Composable
private fun UsernameScreenRender(
    state: UsernameState,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.text,
                onValueChange = onValueChange,
                placeholder = {
                    Text(text = "Введите имя...")
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClick
            ) {
                Text(text = "Присоединиться")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UsernameScreenRenderPreview() {
    KtorchatclientTheme {
        UsernameScreenRender(
            state = UsernameState(
                text = ""
            ),
            onValueChange = {},
            onClick = {}
        )
    }
}