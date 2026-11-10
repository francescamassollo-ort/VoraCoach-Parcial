package com.example.voracoach.ui.screens.nutrition

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalFireDepartment
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.components.VoraTextField
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel
import kotlin.math.roundToInt

// Cantidad de gramos que suma o resta cada toque en "-" / "+"
private const val GRAMS_STEP = 10

/**
 * Un ingrediente de la comida que se está revisando.
 * Los valores nutricionales se guardan para una porción de referencia ([baseGrams]) y se
 * escalan según los gramos elegidos: si 180 g de pollo son 280 kcal, 90 g son 140 kcal.
 * Así las calorías y los macros siempre son proporcionales a la cantidad.
 *
 * [isCustom] marca los ingredientes agregados a mano: el usuario escribe el nombre y las
 * kcal cada 100 g; los macros quedan en 0 porque no los conocemos.
 */
data class IngredientItem(
    val id: Int,
    val name: String,
    val grams: Int,
    val baseGrams: Int,
    val baseCalories: Int,
    val baseProtein: Float = 0f,
    val baseCarbs: Float = 0f,
    val baseFat: Float = 0f,
    val isCustom: Boolean = false,
) {
    private val factor: Float get() = if (baseGrams > 0) grams.toFloat() / baseGrams else 0f
    val calories: Int get() = (baseCalories * factor).roundToInt()
    val protein: Float get() = baseProtein * factor
    val carbs: Float get() = baseCarbs * factor
    val fat: Float get() = baseFat * factor
}

// Lo que "detectó" el análisis de voz o de foto (de demostración, igual que en Figma):
// 280 + 190 + 50 = 520 kcal, P 38 g, C 54 g, F 14 g.
private fun detectedIngredients() = listOf(
    IngredientItem(1, "Grilled Chicken Breast", grams = 180, baseGrams = 180, baseCalories = 280,
        baseProtein = 32f, baseCarbs = 0f, baseFat = 8f),
    IngredientItem(2, "Basmati Rice", grams = 150, baseGrams = 150, baseCalories = 190,
        baseProtein = 4f, baseCarbs = 42f, baseFat = 1f),
    IngredientItem(3, "Mixed Steamed Vegetables", grams = 100, baseGrams = 100, baseCalories = 50,
        baseProtein = 2f, baseCarbs = 12f, baseFat = 5f),
)

// Nombre visible de cada tipo de comida (para "Add to Lunch")
@StringRes
private fun MealType.labelRes(): Int = when (this) {
    MealType.BREAKFAST -> R.string.meal_type_breakfast
    MealType.LUNCH -> R.string.meal_type_lunch
    MealType.AFTERNOON_SNACK -> R.string.meal_type_snack
    MealType.DINNER -> R.string.meal_type_dinner
}

// Route: recibe la comida armada por la pantalla, la guarda con el ViewModel y navega.
@Composable
fun FoodConfirmationRoute(
    viewModel: NutritionViewModel,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    mealType: String,
    fromAnalysis: Boolean,
) {
    val context = LocalContext.current
    val savedMessage = stringResource(R.string.food_meal_saved)
    FoodConfirmationScreen(
        mealType = mealType,
        fromAnalysis = fromAnalysis,
        onNavigateBack = onNavigateBack,
        onSaveMeal = { name, type, calories, protein, carbs, fat ->
            viewModel.addMeal(name, type, calories, protein, carbs, fat)
            onSaveSuccess()
        },
        // Guarda la comida como plantilla (sin sumarla al día), avisa con un Toast y vuelve.
        // Toast y no Snackbar: el aviso tiene que verse aunque esta pantalla se cierre.
        onSaveWithoutLogging = { name, calories, protein, carbs, fat ->
            viewModel.saveWithoutLogging(name, calories, protein, carbs, fat)
            Toast.makeText(context, savedMessage, Toast.LENGTH_SHORT).show()
            onNavigateBack()
        },
    )
}

// Screen: lista de ingredientes a revisar. La lista es estado propio de la pantalla
// (es un formulario en edición); al confirmar, avisa con onSaveMeal y la Route decide qué hacer.
@Composable
fun FoodConfirmationScreen(
    mealType: String,
    fromAnalysis: Boolean,
    onNavigateBack: () -> Unit,
    onSaveMeal: (name: String, type: MealType, calories: Int, protein: Float, carbs: Float, fat: Float) -> Unit,
    onSaveWithoutLogging: (name: String, calories: Int, protein: Float, carbs: Float, fat: Float) -> Unit,
) {
    // Si viene de voz o foto arranca con lo "detectado"; si se carga a mano, arranca vacía
    val ingredients = remember(fromAnalysis) {
        mutableStateListOf<IngredientItem>().apply { if (fromAnalysis) addAll(detectedIngredients()) }
    }

    val selectedType = remember(mealType) {
        MealType.entries.firstOrNull { it.name.equals(mealType, ignoreCase = true) } ?: MealType.forCurrentTime()
    }
    val typeLabel = stringResource(selectedType.labelRes())

    // Solo cuentan los ingredientes con nombre: uno vacío no es comida
    val named = ingredients.filter { it.name.isNotBlank() }
    val totalCalories = named.sumOf { it.calories }
    val totalProtein = named.sumOf { it.protein.toDouble() }.toFloat()
    val totalCarbs = named.sumOf { it.carbs.toDouble() }.toFloat()
    val totalFat = named.sumOf { it.fat.toDouble() }.toFloat()
    val canSave = named.isNotEmpty()
    val mealName = named.joinToString(", ") { it.name.trim() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.food_review_meal),
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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ingredients.forEachIndexed { index, item ->
                IngredientCard(
                    item = item,
                    onChange = { ingredients[index] = it },
                    onRemove = { ingredients.remove(item) },
                )
            }

            // "+ Add Ingredient": link debajo de la lista, como en Figma
            TextButton(
                onClick = {
                    val newId = (ingredients.maxOfOrNull { it.id } ?: 0) + 1
                    ingredients.add(
                        IngredientItem(id = newId, name = "", grams = 100, baseGrams = 100, baseCalories = 0, isCustom = true)
                    )
                },
                contentPadding = PaddingValues(0.dp),
            ) {
                Text(
                    text = stringResource(R.string.food_add_ingredient),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            MealTotalsCard(
                calories = totalCalories,
                protein = totalProtein,
                carbs = totalCarbs,
                fat = totalFat,
            )

            Spacer(Modifier.height(4.dp))

            // Deshabilitado hasta que haya al menos un ingrediente con nombre
            PrimaryButton(
                text = stringResource(R.string.food_add_to, typeLabel),
                onClick = {
                    onSaveMeal(
                        mealName,
                        selectedType,
                        totalCalories,
                        totalProtein,
                        totalCarbs,
                        totalFat,
                    )
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth(),
            )

            // Guarda la comida en el celu para registrarla otro día desde "Search Database",
            // sin sumarla hoy. Mismas reglas que "Add to …": hace falta un ingrediente con nombre.
            SecondaryButton(
                text = stringResource(R.string.food_save_without_logging),
                onClick = { onSaveWithoutLogging(mealName, totalCalories, totalProtein, totalCarbs, totalFat) },
                enabled = canSave,
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// Tarjeta de un ingrediente: nombre y kcal arriba; gramos con "-" / "+" y "Remove" abajo
@Composable
private fun IngredientCard(
    item: IngredientItem,
    onChange: (IngredientItem) -> Unit,
    onRemove: () -> Unit,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        if (item.isCustom) {
            // Ingrediente agregado a mano: se escriben el nombre y las kcal cada 100 g
            VoraTextField(
                label = stringResource(R.string.food_ingredient_name),
                value = item.name,
                onValueChange = { onChange(item.copy(name = it)) },
                placeholder = stringResource(R.string.food_ingredient_name_hint),
                leadingIcon = Icons.Outlined.Edit,
            )
            Spacer(Modifier.height(8.dp))
            VoraTextField(
                label = stringResource(R.string.food_kcal_per_100g),
                value = if (item.baseCalories == 0) "" else item.baseCalories.toString(),
                onValueChange = { text ->
                    val kcal = text.filter { it.isDigit() }.take(4).toIntOrNull() ?: 0
                    onChange(item.copy(baseCalories = kcal))
                },
                placeholder = "0",
                leadingIcon = Icons.Outlined.LocalFireDepartment,
                keyboardType = KeyboardType.Number,
            )
            Spacer(Modifier.height(8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!item.isCustom) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            Text(
                text = stringResource(R.string.food_kcal_value, item.calories),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepButton(
                    icon = Icons.Outlined.Remove,
                    contentDescription = stringResource(R.string.food_decrease_grams),
                    onClick = { onChange(item.copy(grams = (item.grams - GRAMS_STEP).coerceAtLeast(GRAMS_STEP))) },
                )
                Text(
                    text = stringResource(R.string.food_grams_value, item.grams),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp),
                )
                StepButton(
                    icon = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.food_increase_grams),
                    onClick = { onChange(item.copy(grams = item.grams + GRAMS_STEP)) },
                )
            }
            TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = stringResource(R.string.food_remove),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

// Botón cuadrado gris de "-" / "+" para los gramos
@Composable
private fun StepButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

// Tarjeta verde oscura con los totales de la comida (kcal y macros)
@Composable
private fun MealTotalsCard(calories: Int, protein: Float, carbs: Float, fat: Float) {
    AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.food_meal_totals),
                style = MaterialTheme.typography.labelSmall,
                color = Mint,
            )
            Text(
                text = stringResource(R.string.food_kcal_value, calories),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
        }
        Spacer(Modifier.height(12.dp))
        HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            MacroMetric(stringResource(R.string.nutrition_protein), protein, Alignment.Start)
            MacroMetric(stringResource(R.string.nutrition_carbs), carbs, Alignment.CenterHorizontally)
            MacroMetric(stringResource(R.string.nutrition_fat), fat, Alignment.End)
        }
    }
}

@Composable
private fun MacroMetric(label: String, grams: Float, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = Mint,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(R.string.food_grams_value, grams.roundToInt()),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun FoodConfirmationPreview() {
    VoraCoachTheme {
        FoodConfirmationScreen(
            mealType = MealType.LUNCH.name,
            fromAnalysis = true,
            onNavigateBack = {},
            onSaveMeal = { _, _, _, _, _, _ -> },
            onSaveWithoutLogging = { _, _, _, _, _ -> },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodConfirmationEmptyPreview() {
    VoraCoachTheme {
        FoodConfirmationScreen(
            mealType = MealType.DINNER.name,
            fromAnalysis = false,
            onNavigateBack = {},
            onSaveMeal = { _, _, _, _, _, _ -> },
            onSaveWithoutLogging = { _, _, _, _, _ -> },
        )
    }
}
