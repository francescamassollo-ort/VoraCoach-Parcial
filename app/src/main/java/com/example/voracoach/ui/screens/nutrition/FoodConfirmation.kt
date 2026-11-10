package com.example.voracoach.ui.screens.nutrition

import java.util.Locale
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.components.VoraTextField
import com.example.voracoach.ui.theme.BorderGray
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel

data class IngredientItem(
    val id: Int,
    val name: String = "",
    val grams: Int = 0,
    val calories: Int = 0,
)

// Route: recibe la comida armada por la pantalla, la guarda con el ViewModel y navega.
@Composable
fun FoodConfirmationRoute(
    viewModel: NutritionViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    mealType: String = "BREAKFAST",
) {
    FoodConfirmationScreen(
        mealType = mealType,
        onNavigateBack = onNavigateBack,
        onSaveMeal = { name, type, calories, protein, carbs, fat ->
            viewModel.addMeal(name, type, calories, protein, carbs, fat)
            onSaveSuccess()
        },
    )
}

// Screen: formulario de ingredientes. La lista que se va cargando es estado propio de la
// pantalla; al confirmar, avisa con onSaveMeal y la Route decide qué hacer.
@Composable
fun FoodConfirmationScreen(
    mealType: String,
    onNavigateBack: () -> Unit,
    onSaveMeal: (name: String, type: MealType, calories: Int, protein: Float, carbs: Float, fat: Float) -> Unit,
) {
    val ingredientList = remember { mutableStateListOf<IngredientItem>() }

    val totalCals = ingredientList.sumOf { it.calories }
    val totalGrams = ingredientList.sumOf { it.grams }
    val totalProtein = totalGrams / 4
    val totalCarbs = totalGrams / 2
    val totalFat = totalGrams / 9

    val selectedType = remember(mealType) {
        MealType.entries.firstOrNull { it.name.equals(mealType, ignoreCase = true) } ?: MealType.BREAKFAST
    }

    val formattedCategoryName = remember(mealType) {
        mealType.replace("_", " ")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun saveAndConfirm() {
        val mealName = if (ingredientList.isNotEmpty() && ingredientList.any { it.name.isNotBlank() }) {
            ingredientList.filter { it.name.isNotBlank() }.joinToString(", ") { it.name }
        } else {
            "$formattedCategoryName Item"
        }

        onSaveMeal(
            mealName,
            selectedType,
            totalCals,
            totalProtein.toFloat(),
            totalCarbs.toFloat(),
            totalFat.toFloat(),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = "Review Meal",
                onBackClick = onNavigateBack,
                modifier = Modifier.statusBarsPadding(),
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
            // Component 1: Add Ingredient Button
            SecondaryButton(
                text = "+ Add Ingredient",
                onClick = {
                    val newId = (ingredientList.maxOfOrNull { it.id } ?: 0) + 1
                    ingredientList.add(IngredientItem(id = newId, name = "", grams = 100, calories = 150))
                },
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )

            // Component 2: Dynamic Ingredient Cards List
            ingredientList.forEachIndexed { index, item ->
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Left Side Column: Name input & Grams counter
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            VoraTextField(
                                label = "Ingredient Name",
                                value = item.name,
                                onValueChange = { newName ->
                                    ingredientList[index] = item.copy(name = newName)
                                },
                                placeholder = "e.g. Oats, Eggs, Chicken",
                                leadingIcon = Icons.Outlined.Edit,
                            )

                            // Grams counter row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        val newGrams = maxOf(0, item.grams - 10)
                                        val newCals = maxOf(0, item.calories - 20)
                                        ingredientList[index] = item.copy(grams = newGrams, calories = newCals)
                                    },
                                    modifier = Modifier.size(28.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Remove,
                                        contentDescription = "Decrease grams",
                                        modifier = Modifier.size(16.dp),
                                    )
                                }

                                Spacer(Modifier.width(4.dp))

                                Text(
                                    text = "${item.grams}g",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )

                                Spacer(Modifier.width(4.dp))

                                IconButton(
                                    onClick = {
                                        ingredientList[index] = item.copy(
                                            grams = item.grams + 10,
                                            calories = item.calories + 20,
                                        )
                                    },
                                    modifier = Modifier.size(28.dp),
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "Increase grams",
                                        modifier = Modifier.size(16.dp),
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        // Right Side Column: Calories & Remove button
                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                text = "${item.calories} kcal",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )

                            Spacer(Modifier.height(12.dp))

                            TextButton(
                                onClick = { ingredientList.remove(item) },
                                contentPadding = PaddingValues(0.dp),
                            ) {
                                Text(
                                    text = "Remove",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }

            // Component 3: Meal Totals Display Summary Card
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Top Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "meal totals",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextSecondary,
                        )
                        Text(
                            text = "$totalCals kcal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = BorderGray)
                    Spacer(Modifier.height(12.dp))

                    // Bottom Row (3 Macro Metrics)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MacroMetric(label = "Protein", value = "${totalProtein}g")
                        MacroMetric(label = "Carbs", value = "${totalCarbs}g")
                        MacroMetric(label = "Fat", value = "${totalFat}g")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Component 4: Contextual Dynamic Action Button
            PrimaryButton(
                text = "Add to $formattedCategoryName",
                onClick = { saveAndConfirm() },
                modifier = Modifier.fillMaxWidth(),
            )

            // Component 5: Secondary Action Button
            SecondaryButton(
                text = "Save without loggin",
                onClick = {},
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MacroMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodConfirmationPreview() {
    VoraCoachTheme {
        FoodConfirmationScreen(
            mealType = "LUNCH",
            onNavigateBack = {},
            onSaveMeal = { _, _, _, _, _, _ -> },
        )
    }
}
