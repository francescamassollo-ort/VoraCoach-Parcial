package com.example.voracoach.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.DataError
import com.example.voracoach.data.repository.WorkoutRepository
import com.example.voracoach.data.repository.toDataError
import com.example.voracoach.model.Workout
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estados de la pantalla de detalle. A diferencia del catálogo, acá importa si el
// entrenamiento ya estaba guardado: sin caché y sin red no hay nada que mostrar (RF02).
sealed interface WorkoutDetailUiState {
    data object Loading : WorkoutDetailUiState

    // El servidor dijo que el entrenamiento no existe (404)
    data object NotFound : WorkoutDetailUiState

    // Nunca se descargó y la consulta falló: "todavía no está disponible sin conexión"
    data class Unavailable(val error: DataError) : WorkoutDetailUiState

    data class Content(
        val workout: Workout,
        val isRefreshing: Boolean,
        val refreshError: DataError?
    ) : WorkoutDetailUiState
}

@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    // SavedStateHandle trae los argumentos de la ruta (workout/{workoutId}). Al leer el id de acá,
    // y no de una variable de la pantalla, la selección se conserva al rotar (NAV04, RF07).
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val workoutId: String = checkNotNull(savedStateHandle[WORKOUT_ID_ARG]) {
        "Falta el argumento $WORKOUT_ID_ARG en la ruta del detalle"
    }

    private data class RefreshStatus(
        val isRefreshing: Boolean = false,
        val error: DataError? = null
    )

    private val refreshStatus = MutableStateFlow(RefreshStatus())

    val uiState: StateFlow<WorkoutDetailUiState> =
        combine(repository.observeWorkout(workoutId), refreshStatus) { workout, status ->
            when {
                // Si está en Room se muestra siempre, aunque la actualización haya fallado
                workout != null -> WorkoutDetailUiState.Content(
                    workout = workout,
                    isRefreshing = status.isRefreshing,
                    refreshError = status.error
                )
                status.error == DataError.NotFound -> WorkoutDetailUiState.NotFound
                status.error != null -> WorkoutDetailUiState.Unavailable(status.error)
                else -> WorkoutDetailUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WorkoutDetailUiState.Loading
        )

    // Igual que en el catálogo: se consulta una vez al crear el ViewModel, no en cada recomposición
    init {
        refresh()
    }

    fun retry() = refresh()

    private fun refresh() {
        if (refreshStatus.value.isRefreshing) return
        refreshStatus.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            val result = repository.refreshWorkout(workoutId)
            refreshStatus.value = RefreshStatus(
                isRefreshing = false,
                error = result.exceptionOrNull()?.toDataError()
            )
        }
    }

    companion object {
        // Nombre del argumento en la ruta. La ruta en AppDestination usa esta misma constante,
        // así el nombre no se escribe dos veces y no puede quedar distinto.
        const val WORKOUT_ID_ARG = "workoutId"
    }
}
