package com.example.voracoach.ui.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.voracoach.R
import com.example.voracoach.data.repository.DataError
import com.example.voracoach.model.WorkoutLevel

// Textos que dependen de un dato: cada valor se traduce a su string de recursos.
// Están en un solo lugar para que el catálogo y el detalle muestren lo mismo.

@Composable
fun WorkoutLevel.label(): String = stringResource(
    when (this) {
        WorkoutLevel.BEGINNER -> R.string.workout_level_beginner
        WorkoutLevel.INTERMEDIATE -> R.string.workout_level_intermediate
        WorkoutLevel.ADVANCED -> R.string.workout_level_advanced
        WorkoutLevel.UNKNOWN -> R.string.workout_level_unknown
    }
)

// Como DataError es sealed, si se agrega un caso nuevo este "when" deja de compilar
// hasta que se le asigne un mensaje: ningún error queda sin texto.
@Composable
fun DataError.message(): String = when (this) {
    DataError.NoConnection -> stringResource(R.string.error_no_connection)
    is DataError.Server -> stringResource(R.string.error_server, httpCode)
    DataError.InvalidResponse -> stringResource(R.string.error_invalid_response)
    DataError.NotFound -> stringResource(R.string.error_not_found)
    DataError.Unknown -> stringResource(R.string.error_unknown)
}
