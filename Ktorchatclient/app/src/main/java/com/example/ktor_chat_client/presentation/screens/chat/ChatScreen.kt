package com.example.ktor_chat_client.presentation.screens.chat

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ktor_chat_client.domain.model.Message
import com.example.ktor_chat_client.presentation.ui.theme.KtorchatclientTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    username: String?
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collectLatest {
            when (it) {
                ChatEvent.ToastError -> {
                    Toast.makeText(context, "Что-то пошло не так...", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.onAction(action = ChatAction.Connect)
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.onAction(action = ChatAction.Disconnect)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ChatScreenRender(
        username = username,
        state = state,
        onValueChange = {
            viewModel.onAction(ChatAction.OnMessageChange(text = it))
        },
        onClick = {
            viewModel.onAction(ChatAction.OnSendMessage)
        }
    )
}

@Composable
private fun ChatScreenRender(
    username: String?,
    state: ChatState,
    onValueChange: (String) -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(state.messages) {
                val isOwn = it.username == username

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isOwn) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        modifier = Modifier
                            .width(200.dp)
                            .drawBehind {
                                messageItem(isOwnMessage = isOwn)
                            }
                            .background(
                                color = if (isOwn) Color.Green else Color.DarkGray,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp)
                    ) {
                        val colorText = if (isOwn) Color.Black else Color.White
                        Text(
                            text = it.username,
                            fontWeight = FontWeight.Bold,
                            color = colorText
                        )
                        Text(
                            text = it.text,
                            color = colorText
                        )
                        Text(
                            modifier = Modifier.align(Alignment.End),
                            text = it.formattedTime,
                            color = colorText
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = state.textMessage,
                onValueChange = onValueChange,
                placeholder = {
                    Text(text = "Enter a message")
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send"
                )
            }
        }
    }
}

private fun DrawScope.messageItem(
    isOwnMessage: Boolean
) {
    val cornerRadius = 10.dp.toPx()
    val triangleHeight = 20.dp.toPx()
    val triangleWidth = 25.dp.toPx()
    val trianglePath = Path().apply {
        if (isOwnMessage) {
            moveTo(size.width, size.height - cornerRadius)
            lineTo(size.width, size.height + triangleHeight)
            lineTo(size.width - triangleWidth, size.height - cornerRadius)
            close()
        } else {
            moveTo(0f, size.height - cornerRadius)
            lineTo(0f, size.height + triangleHeight)
            lineTo(triangleWidth, size.height - cornerRadius)
            close()
        }
    }
    drawPath(
        path = trianglePath,
        color = if (isOwnMessage) Color.Green else Color.DarkGray
    )
}

@Preview
@Composable
private fun ChatScreenRenderPreview() {
    KtorchatclientTheme {
        ChatScreenRender(
            username = "Шнурок",
            state = ChatState(
                textMessage = "test",
                isLoading = false,
                messages = listOf(
                    Message(
                        text = "Привет",
                        formattedTime = "12:02",
                        username = "Шнурок"
                    ),
                    Message(
                        text = "Привет",
                        formattedTime = "12:01",
                        username = "Сашок"
                    ),
                    Message(
                        text = "Че каво?",
                        formattedTime = "12:00",
                        username = "Шнурок"
                    )
                )
            ),
            onValueChange = {},
            onClick = {}
        )
    }
}