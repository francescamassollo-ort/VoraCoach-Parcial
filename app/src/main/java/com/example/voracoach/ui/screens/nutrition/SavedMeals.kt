package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.SavedMeal
import com.example.voracoach.ui.components.AppListRow
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.VoraTextField
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel

// Route: conecta el buscador y la lista con el ViewModel. Registrar una comida guardada
// la suma al día (con el horario según la hora) y vuelve al Dashboard.
@Composable
fun SavedMealsRoute(
    viewModel: NutritionViewModel,
    onNavigateBack: () -> Unit,
    onMealLogged: () -> Unit,
) {
    val query by viewModel.savedMealsQuery.collectAsStateWithLifecycle()
    val savedMeals by viewModel.savedMeals.collectAsStateWithLifecycle()
    SavedMealsScreen(
        query = query,
        savedMeals = savedMeals,
        onQueryChange = viewModel::onSavedMealsQueryChange,
        onNavigateBack = onNavigateBack,
        onLogMeal = { saved ->
            viewModel.logSavedMeal(saved)
            onMealLogged()
        },
        onDeleteMeal = viewModel::deleteSavedMeal,
    )
}

// Screen: buscador arriba y lista de comidas guardadas. No conoce al ViewModel.
@Composable
fun SavedMealsScreen(
    query: String,
    savedMeals: List<SavedMeal>,
    onQueryChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onLogMeal: (SavedMeal) -> Unit,
    onDeleteMeal: (SavedMeal) -> Unit,
) {
    // Comida a borrar: mientras no sea null se muestra el diálogo de confirmación
    var mealToDelete by remember { mutableStateOf<SavedMeal?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.saved_meals_title),
                onBackClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            VoraTextField(
                label = stringResource(R.string.saved_meals_search_label),
                value = query,
                onValueChange = onQueryChange,
                placeholder = stringResource(R.string.saved_meals_search_hint),
                leadingIcon = Icons.Outlined.Search,
            )
            Spacer(Modifier.height(16.dp))

            if (savedMeals.isEmpty()) {
                // Estado vacío: distinto si no hay nada guardado o si la búsqueda no encontró nada
                EmptySavedMeals(isSearching = query.isNotBlank())
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    items(savedMeals, key = { it.id }) { saved ->
                        AppListRow(
                            icon = Icons.Outlined.Bookmark,
                            title = saved.name,
                            subtitle = stringResource(
                                R.string.saved_meals_macros,
                                saved.protein.toInt(),
                                saved.carbs.toInt(),
                                saved.fat.toInt(),
                            ),
                            onClick = { onLogMeal(saved) },
                            trailing = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.food_kcal_value, saved.calories),
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    )
                                    IconButton(
                                        onClick = { mealToDelete = saved },
                                        modifier = Modifier.size(32.dp),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = stringResource(R.string.saved_meals_delete),
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp),
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    mealToDelete?.let { saved ->
        AlertDialog(
            onDismissRequest = { mealToDelete = null },
            title = { Text(stringResource(R.string.saved_meals_delete_title)) },
            text = { Text(stringResource(R.string.saved_meals_delete_body, saved.name)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMeal(saved)
                        mealToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text(stringResource(R.string.nutrition_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { mealToDelete = null }) {
                    Text(stringResource(R.string.nutrition_cancel), color = TextSecondary)
                }
            },
        )
    }
}

@Composable
private fun EmptySavedMeals(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(
                if (isSearching) R.string.saved_meals_no_results else R.string.saved_meals_empty
            ),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.saved_meals_empty_body),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedMealsPreview() {
    VoraCoachTheme {
        SavedMealsScreen(
            query = "",
            savedMeals = listOf(
                SavedMeal(1, "Chicken, rice and vegetables", 520, 38f, 54f, 14f),
                SavedMeal(2, "Protein smoothie", 310, 30f, 35f, 6f),
            ),
            onQueryChange = {},
            onNavigateBack = {},
            onLogMeal = {},
            onDeleteMeal = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SavedMealsEmptyPreview() {
    VoraCoachTheme {
        SavedMealsScreen(
            query = "pizza",
            savedMeals = emptyList(),
            onQueryChange = {},
            onNavigateBack = {},
            onLogMeal = {},
            onDeleteMeal = {},
        )
    }
}
