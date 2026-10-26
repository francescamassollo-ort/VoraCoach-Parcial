package com.example.voracoach.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voracoach.data.repository.DataError
import com.example.voracoach.data.repository.WorkoutRepository
import com.example.voracoach.data.repository.toDataError
import com.example.voracoach.model.WorkoutSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estados posibles de la pantalla del catálogo (RF05). Es sealed, la pantalla hace un "when"  y el compilador la obliga a contemplar los cuatro casos.
sealed interface WorkoutCatalogUiState {
    data object Loading : WorkoutCatalogUiState
    data object Empty : WorkoutCatalogUiState
    data class Error(val error: DataError) : WorkoutCatalogUiState

    // Hay datos para mostrar (de la red o de la caché). Si un refresco falló, refreshError tiene el motivo, la lista se sigue mostrando y la pantalla agrega un aviso con reintento.
    data class Content(
        val workouts: List<WorkoutSummary>,
        val isRefreshing: Boolean,
        val refreshError: DataError?
    ) : WorkoutCatalogUiState
}

// @HiltViewModel: Hilt crea el ViewModel y le pasa el repositorio por constructor.
// La pantalla lo pide con hiltViewModel() y nunca lo construye a mano.
@HiltViewModel
class WorkoutCatalogViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    // Cómo va la descarga desde la API.
    private data class RefreshStatus( //privado para uqe solo lo modifique el vwmd
        val isRefreshing: Boolean = false,
        val error: DataError? = null,
        val hasLoadedOnce: Boolean = false
    )

    private val refreshStatus = MutableStateFlow(RefreshStatus())

    // El estado de la pantalla sale de combinar lo guardado en Room con cómo va la descarga.
    // Cada vez que cambia cualquiera de los dos, se recalcula.
    val uiState: StateFlow<WorkoutCatalogUiState> =
        combine(repository.observeCatalog(), refreshStatus) { workouts, status ->
            when {
                workouts.isNotEmpty() -> WorkoutCatalogUiState.Content(
                    workouts = workouts,
                    isRefreshing = status.isRefreshing,
                    refreshError = status.error
                )
                status.error != null -> WorkoutCatalogUiState.Error(status.error)
                status.hasLoadedOnce && !status.isRefreshing -> WorkoutCatalogUiState.Empty
                else -> WorkoutCatalogUiState.Loading
            }
        }.stateIn(
            scope = viewModelScope,
            // Mantiene la suscripción 5 s después de que la pantalla deja de observar,
            // al rotar, la pantalla se recrea en menos tiempo y no se reinicia nada.
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WorkoutCatalogUiState.Loading
        )

    // Se descarga una sola vez, al crearse el ViewModel. Como el ViewModel sobrevive a la
    // rotación, girar la pantalla no repite la consulta (RF07).
    init {
        refresh()
    }

    // Lo llama el botón "Reintentar"
    fun retry() = refresh()

    private fun refresh() {
        // Si ya hay una descarga en curso, no se lanza otra (evita pedidos duplicados)
        if (refreshStatus.value.isRefreshing) return
        refreshStatus.update { it.copy(isRefreshing = true, error = null) }

        viewModelScope.launch {
            val result = repository.refreshCatalog()
            refreshStatus.update {
                RefreshStatus(
                    isRefreshing = false,
                    error = result.exceptionOrNull()?.toDataError(),
                    hasLoadedOnce = it.hasLoadedOnce || result.isSuccess
                )
            }
        }
    }
}
