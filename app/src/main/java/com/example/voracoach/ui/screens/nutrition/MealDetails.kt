package com.example.voracoach.ui.screens.nutrition

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealIngredient
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.theme.BorderGray
import com.example.voracoach.ui.theme.SurfaceWhite
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.MealDetailsUiState
import com.example.voracoach.viewmodel.NutritionViewModel

// Route: observa la comida y sus ingredientes en Room y conecta borrar, editar y duplicar.
@Composable
fun MealDetailsRoute(
    mealId: Int,
    viewModel: NutritionViewModel,
    onNavigateBack: () -> Unit,
    onEditMeal: (mealId: Int, type: MealType) -> Unit,
    onMealDuplicated: () -> Unit,
) {
    // remember(mealId): el Flow se crea una sola vez por comida, no en cada recomposición
    val uiState by remember(mealId) { viewModel.observeMealDetails(mealId) }
        .collectAsStateWithLifecycle(initialValue = MealDetailsUiState())
    val context = LocalContext.current
    val duplicatedMessage = stringResource(R.string.meal_details_duplicated)

    MealDetailsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onDeleteMeal = viewModel::removeMeal,
        onEditMeal = { meal -> onEditMeal(meal.id, meal.type) },
        onDuplicateMeal = { meal ->
            viewModel.duplicateMeal(meal.id)
            Toast.makeText(context, duplicatedMessage, Toast.LENGTH_SHORT).show()
            onMealDuplicated()
        },
    )
}

// Screen: dibuja el detalle según el Figma (08 — Meal Details). No conoce al ViewModel.
@Composable
fun MealDetailsScreen(
    uiState: MealDetailsUiState,
    onNavigateBack: () -> Unit,
    onDeleteMeal: (Meal) -> Unit,
    onEditMeal: (Meal) -> Unit,
    onDuplicateMeal: (Meal) -> Unit,
) {
    // Mientras Room responde no se dibuja nada (evita mostrar "no encontrada" un instante)
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize())
        return
    }
    // Sin comida no hay nada que mostrar ni borrar: estado vacío con opción de volver
    val meal = uiState.meal
    if (meal == null) {
        MealNotFound(onNavigateBack = onNavigateBack)
        return
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Título: el horario ("Lunch"); a la derecha, la hora en que se registró
            ScreenHeader(
                title = stringResource(meal.type.labelRes()),
                onBackClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
                actions = {
                    if (meal.time.isNotBlank()) {
                        Text(
                            text = meal.time,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = stringResource(R.string.meal_details_delete),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TotalsCard(meal = meal, calorieGoal = uiState.calorieGoal)

            // Aviso de VORA: cuánta proteína falta para el objetivo del día
            InsightBanner(
                message = if (uiState.proteinRemaining > 0) {
                    stringResource(R.string.meal_details_insight_protein, uiState.proteinRemaining)
                } else {
                    stringResource(R.string.meal_details_insight_done)
                },
            )

            Text(
                text = stringResource(R.string.meal_details_ingredients),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (uiState.ingredients.isEmpty()) {
                // Comidas registradas desde "Search Database" guardan solo los totales
                Text(
                    text = stringResource(R.string.meal_details_no_ingredients),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.ingredients.forEach { ingredient -> IngredientRow(ingredient) }
                }
            }

            Spacer(Modifier.height(4.dp))

            SecondaryButton(
                text = stringResource(R.string.meal_details_edit),
                onClick = { onEditMeal(meal) },
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton(
                text = stringResource(R.string.meal_details_duplicate),
                onClick = { onDuplicateMeal(meal) },
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.meal_details_delete),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.meal_details_delete_body, meal.name),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMeal(meal)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = SurfaceWhite,
                    ),
                ) {
                    Text(stringResource(R.string.nutrition_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.nutrition_cancel), color = TextSecondary)
                }
            },
        )
    }
}

// Tarjeta de totales: kcal grandes, anillo con la parte del objetivo diario que representa
// esta comida, y los macros abajo
@Composable
private fun TotalsCard(meal: Meal, calorieGoal: Int) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(R.string.meal_details_total_calories),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.food_kcal_value, meal.calories),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            CircularProgressIndicator(
                progress = { if (calorieGoal > 0) (meal.calories.toFloat() / calorieGoal).coerceIn(0f, 1f) else 0f },
                modifier = Modifier.size(64.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = BorderGray,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round,
            )
        }
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = BorderGray)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MacroValue(stringResource(R.string.nutrition_protein), meal.protein, Alignment.Start)
            MacroValue(stringResource(R.string.nutrition_carbs), meal.carbs, Alignment.CenterHorizontally)
            MacroValue(stringResource(R.string.nutrition_fat), meal.fat, Alignment.End)
        }
    }
}

@Composable
private fun MacroValue(label: String, grams: Float, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.food_grams_value, grams.toInt()),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

// Fila de un ingrediente: nombre y gramos a la izquierda, kcal a la derecha
@Composable
private fun IngredientRow(ingredient: MealIngredient) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(R.string.food_grams_value, ingredient.grams),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.food_kcal_value, ingredient.calories),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

// Estado vacío: la comida pedida no está en la base (por ejemplo, se borró)
@Composable
private fun MealNotFound(onNavigateBack: () -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.nutrition_meal_detail),
                onBackClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.nutrition_meal_not_found),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.nutrition_meal_not_found_body),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(24.dp))
            PrimaryButton(
                text = stringResource(R.string.nutrition_back_to_meals),
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun MealDetailsPreview() {
    VoraCoachTheme {
        MealDetailsScreen(
            uiState = MealDetailsUiState(
                isLoading = false,
                meal = Meal(
                    id = 1,
                    name = "Chicken, rice and vegetables",
                    date = "2026-11-10",
                    time = "13:12",
                    type = MealType.LUNCH,
                    calories = 520,
                    protein = 38f,
                    carbs = 54f,
                    fat = 14f,
                ),
                ingredients = listOf(
                    MealIngredient(name = "Grilled Chicken Breast", grams = 180, calories = 280, protein = 32f, carbs = 0f, fat = 8f),
                    MealIngredient(name = "Basmati Rice", grams = 150, calories = 190, protein = 4f, carbs = 42f, fat = 1f),
                    MealIngredient(name = "Mixed Steamed Vegetables", grams = 100, calories = 50, protein = 2f, carbs = 12f, fat = 5f),
                ),
                proteinRemaining = 36,
            ),
            onNavigateBack = {},
            onDeleteMeal = {},
            onEditMeal = {},
            onDuplicateMeal = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MealNotFoundPreview() {
    VoraCoachTheme {
        MealNotFound(onNavigateBack = {})
    }
}
