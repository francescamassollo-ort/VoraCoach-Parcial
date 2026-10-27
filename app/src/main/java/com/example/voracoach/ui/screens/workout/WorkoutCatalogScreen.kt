package com.example.voracoach.ui.screens.workout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.data.repository.DataError
import com.example.voracoach.model.WorkoutLevel
import com.example.voracoach.model.WorkoutSummary
import com.example.voracoach.viewmodel.WorkoutCatalogUiState
import com.example.voracoach.viewmodel.WorkoutCatalogViewModel

// Pantalla "Workout / 01 — Dashboard": catálogo de entrenamientos desde la API (RF01).
// - WorkoutCatalogRoute: obtiene el ViewModel (Hilt) y observa su estado. Es lo que usa el NavHost.
// - WorkoutCatalogScreen: solo dibuja el estado que recibe. No conoce el ViewModel, así se puede previsualizar con datos de ejemplo y probar cada estado por separado.
@Composable
fun WorkoutCatalogRoute(
    onWorkoutClick: (workoutId: String) -> Unit,
    onWeeklyPlanClick: () -> Unit,
    viewModel: WorkoutCatalogViewModel = hiltViewModel()
) {
    // collectAsStateWithLifecycle deja de observar cuando la app pasa a segundo plano,
    // así no se procesan cambios que nadie está viendo
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    WorkoutCatalogScreen(
        uiState = uiState,
        onRetry = viewModel::retry,
        onWorkoutClick = onWorkoutClick,
        onWeeklyPlanClick = onWeeklyPlanClick
    )
}

// onWorkoutClick recibe el id del entrenamiento tocado: la pantalla no sabe a dónde se navega,
// solo avisa cuál se eligió y el NavHost abre su detalle
@Composable
fun WorkoutCatalogScreen(
    uiState: WorkoutCatalogUiState,
    onRetry: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit,
    onWeeklyPlanClick: () -> Unit = {}
) {
    // La pantalla no tiene Scaffold propio, así que deja lugar para la barra de estado del celular
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.workout_catalog_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.workout_catalog_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
            // Calendario de Figma (ícono exportado en res/drawable/calendar.xml): abre el plan semanal
            CircleIconButton(
                icon = ImageVector.vectorResource(R.drawable.calendar),
                contentDescription = stringResource(R.string.weekly_open_plan),
                onClick = onWeeklyPlanClick
            )
        }
        Spacer(Modifier.height(16.dp))

        // Un "when" sobre el estado sealed: el compilador obliga a contemplar los 4 casos de RF05
        when (uiState) {
            WorkoutCatalogUiState.Loading -> CenteredBox { CircularProgressIndicator() }

            WorkoutCatalogUiState.Empty -> CenteredBox {
                Text(
                    text = stringResource(R.string.workout_catalog_empty),
                    textAlign = TextAlign.Center
                )
            }

            is WorkoutCatalogUiState.Error -> CenteredBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = uiState.error.message(), textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
                }
            }

            is WorkoutCatalogUiState.Content -> CatalogContent(uiState, onRetry, onWorkoutClick)
        }
    }
}

@Composable
private fun CatalogContent(
    state: WorkoutCatalogUiState.Content,
    onRetry: () -> Unit,
    onWorkoutClick: (workoutId: String) -> Unit
) {
    // Barra fina mientras se actualiza, sin tapar la lista que ya se ve
    if (state.isRefreshing) {
        LinearProgressIndicator(Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
    }

    // La red falló pero hay caché, se avisa y se ofrece reintentar, sin ocultar los datos (RF05)
    if (state.refreshError != null) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.workout_catalog_refresh_failed),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onRetry) { Text(stringResource(R.string.action_retry)) }
            }
        }
        Spacer(Modifier.height(12.dp))
    }

    // LazyColumn solo dibuja las tarjetas visibles. key = id ayuda a Compose a reconocer
    // cada tarjeta cuando la lista cambia, en vez de redibujarlas todas.
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(state.workouts, key = { it.id }) { workout ->
            WorkoutCard(workout, onClick = { onWorkoutClick(workout.id) })
        }
    }
}

@Composable
private fun WorkoutCard(workout: WorkoutSummary, onClick: () -> Unit) {
    // Card con onClick: toda la tarjeta es tocable y muestra el efecto al presionarla.
    // Blanca con borde gris como en Figma (por defecto Material la pinta con un violeta claro).
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                text = workout.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(
                    R.string.workout_catalog_meta,
                    workout.level.label(),
                    workout.durationMinutes,
                    workout.exerciseCount
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CenteredBox(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
}

private val sampleWorkouts = listOf(
    WorkoutSummary("w01", "Fuerza inicial", WorkoutLevel.BEGINNER, 25, 3),
    WorkoutSummary("w02", "Entrenamiento completo", WorkoutLevel.INTERMEDIATE, 35, 3)
)

@Preview(showBackground = true)
@Composable
private fun CatalogContentPreview() {
    MaterialTheme {
        WorkoutCatalogScreen(
            uiState = WorkoutCatalogUiState.Content(sampleWorkouts, isRefreshing = false, refreshError = null),
            onRetry = {},
            onWorkoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogCachedWithErrorPreview() {
    MaterialTheme {
        WorkoutCatalogScreen(
            uiState = WorkoutCatalogUiState.Content(sampleWorkouts, isRefreshing = false, refreshError = DataError.NoConnection),
            onRetry = {},
            onWorkoutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogErrorPreview() {
    MaterialTheme {
        WorkoutCatalogScreen(uiState = WorkoutCatalogUiState.Error(DataError.Server(503)), onRetry = {}, onWorkoutClick = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun CatalogEmptyPreview() {
    MaterialTheme {
        WorkoutCatalogScreen(uiState = WorkoutCatalogUiState.Empty, onRetry = {}, onWorkoutClick = {})
    }
}
