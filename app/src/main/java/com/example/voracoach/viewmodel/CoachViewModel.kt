package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// Los 6 estados del asistente en Figma (Voice / 01 a 06). Es sealed: la pantalla hace un "when"
// y el compilador la obliga a dibujar todos. Todo es simulado: no hay reconocimiento de voz,
// IA ni audio reales, y no se pide permiso de micrófono (NAV05).
sealed interface CoachState {
    data object Idle : CoachState

    // transcript crece de a poco, como si se fuera transcribiendo lo que dice el usuario
    data class Listening(val transcript: String, val isComplete: Boolean) : CoachState

    data class Processing(val userText: String) : CoachState

    data class Speaking(
        val reply: String,
        val elapsedSeconds: Int,
        val totalSeconds: Int,
        val isPaused: Boolean
    ) : CoachState

    data object Conversation : CoachState

    // showPermissionCard: la tarjeta "Microphone access needed" se puede cerrar con "Not Now"
    data class Error(val showPermissionCard: Boolean) : CoachState
}

// Preguntas sugeridas de la pantalla Idle. Es un enum para que la pantalla elija el texto
// (strings.xml) y el ViewModel elija la respuesta, sin comparar textos.
enum class CoachSuggestion { START_WORKOUT, LOG_BREAKFAST, EAT_TONIGHT, HOW_AM_I_DOING }

data class ChatMessage(
    val id: Long,
    val fromUser: Boolean,
    val text: String,
    val time: String
)

data class CoachUiState(
    val state: CoachState = CoachState.Idle,
    val messages: List<ChatMessage> = emptyList()
)

// Contenido de demostración, en un solo lugar (como el perfil de Home)
private const val DEMO_TRANSCRIPT = "I had chicken, rice and some vegetables for lunch."
private const val DEMO_TRANSCRIPT_REPLY =
    "That sounds like a balanced lunch. I've estimated around 520 calories with approximately 38 grams of protein."

private val SUGGESTION_REPLIES = mapOf(
    CoachSuggestion.START_WORKOUT to "Great! Your recommended session is ready in the Workout tab. Start with a light warm-up.",
    CoachSuggestion.LOG_BREAKFAST to "Sure. Open Nutrition and tap Add to log your breakfast manually.",
    CoachSuggestion.EAT_TONIGHT to "You're 36g short of your protein target. Try grilled chicken with roasted vegetables.",
    CoachSuggestion.HOW_AM_I_DOING to "You've completed 4 workouts this week. Your consistency is trending up!"
)

// Respuestas para lo que se escribe en la conversación: se van alternando
private val TYPED_REPLIES = listOf(
    "Got it. I'll take that into account for your plan today.",
    "Good question! Keep your protein high and stay hydrated.",
    "Absolutely. Try grilled tofu with roasted vegetables, quinoa and Greek yoghurt."
)

private const val WORD_DELAY_MS = 250L
private const val PROCESSING_MS = 2_000L
private const val SECONDS_PER_WORD = 0.4

@HiltViewModel
class CoachViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    // La simulación en curso (escucha, procesamiento o habla). Se cancela al cambiar de estado
    // para que una cuenta vieja no pise el estado nuevo. Corre en viewModelScope: al rotar la
    // pantalla sigue donde estaba, porque el ViewModel no se destruye (RF07).
    private var simulation: Job? = null
    private var nextMessageId = 0L
    private var typedReplyIndex = 0

    // --- Idle ---

    fun onTapToTalk() = startListening()

    fun onSuggestionClick(suggestion: CoachSuggestion, questionText: String) {
        val reply = SUGGESTION_REPLIES.getValue(suggestion)
        processAndSpeak(userText = questionText, reply = reply)
    }

    fun onTypeInstead() = goTo(CoachState.Conversation)

    // --- Listening ---

    // Tocar el mic termina la escucha. Si todavía no se entendió la frase completa,
    // se simula que no se entendió y se muestra el estado de error (Voice / 06).
    fun onStopListening() {
        val listening = _uiState.value.state as? CoachState.Listening ?: return
        if (listening.isComplete) processAndSpeak(listening.transcript, DEMO_TRANSCRIPT_REPLY)
        else goTo(CoachState.Error(showPermissionCard = true))
    }

    fun onCancel() = goTo(CoachState.Idle)

    // --- Speaking ---

    fun onTogglePause() = updateSpeaking { it.copy(isPaused = !it.isPaused) }

    fun onReplay() = updateSpeaking { it.copy(elapsedSeconds = 0, isPaused = false) }

    // "Tap to Interrupt" y el botón de detener pasan a la conversación, donde ya está la respuesta
    fun onInterrupt() = goTo(CoachState.Conversation)

    // --- Conversation ---

    fun onSendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        addMessage(fromUser = true, text = trimmed)
        simulation?.cancel()
        simulation = viewModelScope.launch {
            delay(1_200)
            addMessage(fromUser = false, text = TYPED_REPLIES[typedReplyIndex % TYPED_REPLIES.size])
            typedReplyIndex++
        }
    }

    // --- Error ---

    fun onTryAgain() = startListening()

    fun onDismissPermissionCard() {
        val error = _uiState.value.state as? CoachState.Error ?: return
        _uiState.update { it.copy(state = error.copy(showPermissionCard = false)) }
    }

    // Botón atrás: desde cualquier estado vuelve a Idle; desde Idle devuelve false
    // para que la navegación salga de Coach (NAV02)
    fun onBack(): Boolean {
        if (_uiState.value.state == CoachState.Idle) return false
        goTo(CoachState.Idle)
        return true
    }

    // --- Simulación ---

    private fun startListening() {
        simulation?.cancel()
        simulation = viewModelScope.launch {
            val words = DEMO_TRANSCRIPT.split(" ")
            for (count in 1..words.size) {
                setState(CoachState.Listening(words.take(count).joinToString(" "), isComplete = false))
                delay(WORD_DELAY_MS)
            }
            setState(CoachState.Listening(DEMO_TRANSCRIPT, isComplete = true))
            delay(800)
            processAndSpeak(DEMO_TRANSCRIPT, DEMO_TRANSCRIPT_REPLY)
        }
    }

    private fun processAndSpeak(userText: String, reply: String) {
        simulation?.cancel()
        simulation = viewModelScope.launch {
            addMessage(fromUser = true, text = userText)
            setState(CoachState.Processing(userText))
            delay(PROCESSING_MS)

            // La respuesta queda guardada en la conversación aunque se interrumpa al hablar
            addMessage(fromUser = false, text = reply)
            val total = (reply.split(" ").size * SECONDS_PER_WORD).toInt().coerceAtLeast(3)
            setState(CoachState.Speaking(reply, elapsedSeconds = 0, totalSeconds = total, isPaused = false))

            // Avanza un segundo por vez; en pausa no avanza. Al terminar, pasa a la conversación.
            while (true) {
                delay(1_000)
                val speaking = _uiState.value.state as? CoachState.Speaking ?: return@launch
                if (speaking.isPaused) continue
                if (speaking.elapsedSeconds + 1 >= speaking.totalSeconds) break
                setState(speaking.copy(elapsedSeconds = speaking.elapsedSeconds + 1))
            }
            setState(CoachState.Conversation)
        }
    }

    private fun goTo(state: CoachState) {
        simulation?.cancel()
        setState(state)
    }

    private fun setState(state: CoachState) = _uiState.update { it.copy(state = state) }

    private fun updateSpeaking(change: (CoachState.Speaking) -> CoachState.Speaking) {
        val speaking = _uiState.value.state as? CoachState.Speaking ?: return
        setState(change(speaking))
    }

    private fun addMessage(fromUser: Boolean, text: String) {
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        _uiState.update {
            it.copy(messages = it.messages + ChatMessage(nextMessageId++, fromUser, text, time))
        }
    }
}
