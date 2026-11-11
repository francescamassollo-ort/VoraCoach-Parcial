package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealIngredient
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.theme.BorderGray
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.SurfaceWhite
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionUiState
import com.example.voracoach.viewmodel.NutritionViewModel
import java.util.Locale

// Route: conecta la pantalla con el ViewModel.
@Composable
fun TodayMealsRoute(
    viewModel: NutritionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddFoodWithCategory: (String) -> Unit,
    onMealClick: (mealId: Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TodayMealsScreen(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToAddFoodWithCategory = onNavigateToAddFoodWithCategory,
        onMealClick = onMealClick,
    )
}

// Screen: según el Figma (02 — Today's Meals). Una tarjeta por horario con el total arriba y
// los ingredientes de cada comida adentro; los horarios sin comidas muestran una tarjeta
// punteada para agregar. Tocar una comida abre su detalle (ahí se edita o se borra).
@Composable
fun TodayMealsScreen(
    uiState: NutritionUiState,
    onNavigateBack: () -> Unit,
    onNavigateToAddFoodWithCategory: (String) -> Unit,
    onMealClick: (mealId: Int) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = "",
                onBackClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "header") {
                Header(totalCalories = uiState.totalCalories)
            }

            items(MealType.entries, key = { it.name }) { type ->
                val mealsOfType = uiState.meals.filter { it.type == type }
                if (mealsOfType.isEmpty()) {
                    EmptySlotCard(
                        type = type,
                        onAdd = { onNavigateToAddFoodWithCategory(type.name) },
                    )
                } else {
                    MealTypeCard(
                        type = type,
                        meals = mealsOfType,
                        ingredientsByMeal = uiState.ingredientsByMeal,
                        onMealClick = onMealClick,
                    )
                }
            }
        }
    }
}

// "TIMELINE / Today's Meals" a la izquierda y el total del día en una pastilla a la derecha
@Composable
private fun Header(totalCalories: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.today_meals_timeline),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                ),
                color = TextSecondary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.today_meals_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Text(
                // Con separador de miles: "1,175 kcal"
                text = stringResource(
                    R.string.nutrition_goal_kcal_value,
                    String.format(Locale.getDefault(), "%,d", totalCalories),
                ),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

// Tarjeta de un horario: nombre y total arriba, y debajo los ingredientes de cada comida
@Composable
private fun MealTypeCard(
    type: MealType,
    meals: List<Meal>,
    ingredientsByMeal: Map<Int, List<MealIngredient>>,
    onMealClick: (mealId: Int) -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(type.labelRes()),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(R.string.food_kcal_value, meals.sumOf { it.calories }),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(10.dp))
        HorizontalDivider(color = BorderGray)

        meals.forEachIndexed { index, meal ->
            if (index > 0) HorizontalDivider(color = BorderGray, modifier = Modifier.padding(top = 6.dp))
            // Toda la comida es un bloque tocable que abre su detalle
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMealClick(meal.id) }
                    .padding(top = 6.dp),
            ) {
                val ingredients = ingredientsByMeal[meal.id].orEmpty()
                if (ingredients.isEmpty()) {
                    // Comidas sin ingredientes (por ejemplo, desde "Search Database"): solo el nombre
                    ItemRow(name = meal.name, detail = null, calories = meal.calories)
                } else {
                    ingredients.forEach { ingredient ->
                        ItemRow(
                            name = ingredient.name,
                            detail = stringResource(R.string.food_grams_value, ingredient.grams),
                            calories = ingredient.calories,
                        )
                    }
                }
            }
        }
    }
}

// Fila de un ingrediente: nombre y gramos a la izquierda, kcal a la derecha
@Composable
private fun ItemRow(name: String, detail: String?, calories: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (detail != null) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.food_kcal_value, calories),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}

// Horario sin comidas: tarjeta con borde punteado, "Dinner is not logged yet" y "+ Add Dinner"
@Composable
private fun EmptySlotCard(type: MealType, onAdd: () -> Unit) {
    val label = stringResource(type.labelRes())
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = BorderGray,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f)),
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
            }
            .padding(vertical = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.today_meals_not_logged, label),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen, contentColor = SurfaceWhite),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
            ) {
                Text(
                    text = stringResource(R.string.today_meals_add, label),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun TodayMealsPreview() {
    val lunch = Meal(
        id = 2, name = "Chicken, rice and vegetables", date = "2026-11-10", time = "13:12",
        type = MealType.LUNCH, calories = 520, protein = 38f, carbs = 54f, fat = 14f,
    )
    VoraCoachTheme {
        TodayMealsScreen(
            uiState = NutritionUiState(
                meals = listOf(lunch),
                ingredientsByMeal = mapOf(
                    2 to listOf(
                        MealIngredient(mealId = 2, name = "Grilled Chicken Breast", grams = 180, calories = 280, protein = 32f, carbs = 0f, fat = 8f),
                        MealIngredient(mealId = 2, name = "Basmati Rice", grams = 150, calories = 190, protein = 4f, carbs = 42f, fat = 1f),
                        MealIngredient(mealId = 2, name = "Mixed Steamed Vegetables", grams = 100, calories = 50, protein = 2f, carbs = 12f, fat = 5f),
                    )
                ),
                totalCalories = 520,
            ),
            onNavigateBack = {},
            onNavigateToAddFoodWithCategory = {},
            onMealClick = {},
        )
    }
}
