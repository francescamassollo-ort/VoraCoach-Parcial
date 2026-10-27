package com.example.voracoach.ui.screens.coach

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.ui.components.StatusBarIconsFor
import com.example.voracoach.viewmodel.CoachState
import com.example.voracoach.viewmodel.CoachSuggestion
import com.example.voracoach.viewmodel.CoachUiState
import com.example.voracoach.viewmodel.CoachViewModel
import kotlinx.coroutines.launch

// Todo lo que la pantalla de Coach puede avisar al ViewModel. Agruparlo en una clase evita pasar
// trece funciones sueltas de un composable a otro, y en los Previews se usa con lambdas vacías.
data class CoachActions(
    val onBack: () -> Unit = {},
    val onTapToTalk: () -> Unit = {},
    val onSuggestionClick: (CoachSuggestion, String) -> Unit = { _, _ -> },
    val onTypeInstead: () -> Unit = {},
    val onStopListening: () -> Unit = {},
    val onCancel: () -> Unit = {},
    val onTogglePause: () -> Unit = {},
    val onReplay: () -> Unit = {},
    val onInterrupt: () -> Unit = {},
    val onSendMessage: (String) -> Unit = {},
    val onTryAgain: () -> Unit = {},
    val onDismissPermissionCard: () -> Unit = {},
    val onDemoOnly: () -> Unit = {}
)

// Pantalla "Voice / 01 a 06": una sola ruta (coach) que muestra el estado actual del asistente.
@Composable
fun CoachRoute(
    onExit: () -> Unit,
    viewModel: CoachViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Botón atrás del sistema: dentro de Coach vuelve a Idle; desde Idle sale de la pantalla (NAV02)
    BackHandler { if (!viewModel.onBack()) onExit() }

    // Las funciones de servicio (permiso de micrófono, adjuntar) no existen: avisan que es demostración
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)

    val actions = CoachActions(
        onBack = { if (!viewModel.onBack()) onExit() },
        onTapToTalk = viewModel::onTapToTalk,
        onSuggestionClick = viewModel::onSuggestionClick,
        onTypeInstead = viewModel::onTypeInstead,
        onStopListening = viewModel::onStopListening,
        onCancel = viewModel::onCancel,
        onTogglePause = viewModel::onTogglePause,
        onReplay = viewModel::onReplay,
        onInterrupt = viewModel::onInterrupt,
        onSendMessage = viewModel::onSendMessage,
        onTryAgain = viewModel::onTryAgain,
        onDismissPermissionCard = viewModel::onDismissPermissionCard,
        onDemoOnly = { scope.launch { snackbarHostState.showSnackbar(demoMessage) } }
    )

    Box(Modifier.fillMaxSize()) {
        CoachScreen(uiState = uiState, actions = actions)
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}

@Composable
fun CoachScreen(uiState: CoachUiState, actions: CoachActions) {
    // Escucha, procesamiento y habla son pantallas oscuras: la hora y la batería se ven en blanco.
    // Coach cambia de fondo sin cambiar de ruta, por eso lo decide esta pantalla y no el NavHost.
    val state = uiState.state
    StatusBarIconsFor(
        darkBackground = state is CoachState.Listening || state is CoachState.Processing || state is CoachState.Speaking
    )

    when (state) {
        CoachState.Idle -> CoachIdle(lastUserMessage = uiState.messages.lastOrNull { it.fromUser }?.text, actions = actions)
        is CoachState.Error -> CoachError(showPermissionCard = state.showPermissionCard, actions = actions)
        // Provisorio: estos estados se dibujan en los pasos siguientes
        is CoachState.Listening, is CoachState.Processing, is CoachState.Speaking, CoachState.Conversation ->
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state::class.simpleName.orEmpty(), style = MaterialTheme.typography.titleLarge)
            }
    }
}
