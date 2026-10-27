package com.example.voracoach.ui.screens.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.ChatMessage

// "Voice / 05 — Conversation": historial de la sesión y barra para escribir
@Composable
internal fun CoachConversation(messages: List<ChatMessage>, actions: CoachActions) {
    // El texto que se está escribiendo es estado de la pantalla, no del ViewModel:
    // rememberSaveable lo conserva al rotar sin que el ViewModel tenga que conocerlo
    var input by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Cada vez que llega un mensaje nuevo, la lista baja hasta el último
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            // Sube la barra de escritura cuando se abre el teclado
            .imePadding()
    ) {
        ScreenHeader(
            title = stringResource(R.string.coach_title),
            onBackClick = actions.onBack,
            actions = {
                StatusBadge(
                    text = stringResource(R.string.coach_online),
                    style = StatusBadgeStyle.Mint,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        )

        if (messages.isEmpty()) {
            Box(Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.coach_conversation_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                // key: cada burbuja se identifica por su id, así no se redibujan todas al sumar una
                items(messages, key = { it.id }) { message -> ChatBubble(message) }
            }
        }

        InputBar(
            text = input,
            onTextChange = { input = it },
            onSend = {
                actions.onSendMessage(input)
                input = ""
            },
            actions = actions
        )
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    // Usuario a la derecha en verde; VORA a la izquierda en tarjeta clara
    val container = if (message.fromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val content = if (message.fromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val shape = if (message.fromUser) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    Column(
        horizontalAlignment = if (message.fromUser) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!message.fromUser) {
            Text(
                text = stringResource(R.string.coach_vora_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
        }
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = content,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(container, shape)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = message.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    actions: CoachActions
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // Adjuntar no existe: avisa que es demostración
        IconButton(onClick = actions.onDemoOnly) {
            Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.coach_attach))
        }
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = { Text(stringResource(R.string.coach_input_hint)) },
            shape = RoundedCornerShape(24.dp),
            maxLines = 4,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(8.dp))
        // Con texto escrito el botón envía; vacío, vuelve a escuchar (Voice / 02)
        val canSend = text.isNotBlank()
        IconButton(
            onClick = if (canSend) onSend else actions.onTapToTalk,
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                imageVector = if (canSend) Icons.AutoMirrored.Outlined.Send else Icons.Outlined.Mic,
                contentDescription = stringResource(if (canSend) R.string.coach_send else R.string.coach_tap_to_talk),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun CoachConversationPreview() {
    VoraCoachTheme {
        CoachConversation(
            messages = listOf(
                ChatMessage(0, fromUser = true, text = "What should I eat tonight?", time = "19:02"),
                ChatMessage(1, fromUser = false, text = "You're 36g short of your protein target. Try grilled chicken with roasted vegetables.", time = "19:02")
            ),
            actions = CoachActions()
        )
    }
}
