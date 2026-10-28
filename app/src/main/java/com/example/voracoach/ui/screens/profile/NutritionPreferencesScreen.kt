package com.example.voracoach.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.DietType
import com.example.voracoach.model.FavoriteFood
import com.example.voracoach.model.NutritionPriority
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.AppInfoRow
import com.example.voracoach.ui.components.AppOptionCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionPreferencesUiState
import com.example.voracoach.viewmodel.NutritionPreferencesViewModel

// Pantalla "Profile / 04 — Nutrition Preferences". Sub-pantalla: no muestra la barra inferior.
@Composable
fun NutritionPreferencesRoute(
    onBack: () -> Unit,
    viewModel: NutritionPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    NutritionPreferencesScreen(
        uiState = uiState,
        onBack = onBack,
        onDietSelected = viewModel::onDietSelected,
        onFoodToggle = viewModel::onFoodToggle,
        onIngredientChange = viewModel::onIngredientChange,
        onAddIngredient = viewModel::onAddIngredient,
        onRemoveIngredient = viewModel::onRemoveIngredient,
        onPriorityToggle = viewModel::onPriorityToggle,
        // Guarda y vuelve a la pantalla anterior
        onSave = {
            viewModel.onSave()
            onBack()
        }
    )
}

@OptIn(ExperimentalLayoutApi::class) // FlowRow es experimental en versiones viejas de Compose
@Composable
fun NutritionPreferencesScreen(
    uiState: NutritionPreferencesUiState,
    onBack: () -> Unit,
    onDietSelected: (DietType) -> Unit,
    onFoodToggle: (FavoriteFood) -> Unit,
    onIngredientChange: (String) -> Unit,
    onAddIngredient: () -> Unit,
    onRemoveIngredient: (String) -> Unit,
    onPriorityToggle: (NutritionPriority) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ScreenHeader ya trae su margen horizontal: no se le suma otro
        ScreenHeader(title = stringResource(R.string.nutrition_title), onBackClick = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.nutrition_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Dieta: selección única
            Section(stringResource(R.string.nutrition_section_diet)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DietType.entries.forEach { diet ->
                        AppChip(
                            text = stringResource(diet.labelRes()),
                            style = AppChipStyle.Mint,
                            compactTouchArea = true,
                            selected = diet == uiState.diet,
                            onClick = { onDietSelected(diet) }
                        )
                    }
                }
            }

            // Comidas que disfruta: selección múltiple, las elegidas llevan tilde
            Section(stringResource(R.string.nutrition_section_enjoy)) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FavoriteFood.entries.forEach { food ->
                        val selected = food in uiState.favoriteFoods
                        AppChip(
                            text = stringResource(food.labelRes()),
                            style = AppChipStyle.MintOutline,
                            compactTouchArea = true,
                            selected = selected,
                            trailingIcon = if (selected) Icons.Filled.Check else null,
                            onClick = { onFoodToggle(food) }
                        )
                    }
                }
            }

            // Comidas a evitar: se escribe un ingrediente y cada chip se quita tocándolo
            Section(stringResource(R.string.nutrition_section_avoid)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AddIngredientField(uiState.newIngredient, onIngredientChange, onAddIngredient)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        uiState.avoidedFoods.forEach { food ->
                            AppChip(
                                text = food,
                                trailingIcon = Icons.Filled.Cancel,
                                trailingIconTint = MaterialTheme.colorScheme.error,
                                compactTouchArea = true,
                                onClick = { onRemoveIngredient(food) }
                            )
                        }
                    }
                }
            }

            Section(stringResource(R.string.nutrition_section_meals)) {
                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    AppInfoRow(stringResource(R.string.nutrition_meals_per_day), uiState.mealsPerDay)
                    Spacer(Modifier.height(6.dp))
                    AppInfoRow(stringResource(R.string.nutrition_preferred_breakfast), uiState.preferredBreakfast)
                    Spacer(Modifier.height(6.dp))
                    AppInfoRow(stringResource(R.string.nutrition_preferred_dinner), uiState.preferredDinner)
                }
            }

            Section(stringResource(R.string.nutrition_section_priorities)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NutritionPriority.entries.forEach { priority ->
                        AppOptionCard(
                            title = stringResource(priority.labelRes()),
                            selected = priority in uiState.priorities,
                            onClick = { onPriorityToggle(priority) }
                        )
                    }
                }
            }

            // Dentro del scroll, justo después del contenido, como en Figma
            SecondaryButton(
                text = stringResource(R.string.nutrition_save),
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// Campo "+ Add an ingredient...": más bajo que un OutlinedTextField (44 dp), por eso se arma
// con BasicTextField. La tecla "Done" del teclado agrega el ingrediente.
@Composable
private fun AddIngredientField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(12.dp),
        color = scheme.surface,
        border = BorderStroke(1.dp, scheme.outline)
    ) {
        Row(Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = scheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = scheme.onSurface),
                cursorBrush = SolidColor(scheme.primary),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.nutrition_add_ingredient),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextMuted
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun NutritionPreferencesPreview() {
    VoraCoachTheme {
        NutritionPreferencesScreen(
            uiState = NutritionPreferencesUiState(
                diet = DietType.EVERYTHING,
                favoriteFoods = FavoriteFood.entries.toSet(),
                avoidedFoods = listOf("Peanuts", "Shellfish", "Mushrooms"),
                newIngredient = "",
                priorities = NutritionPriority.entries.toSet(),
                mealsPerDay = "3 meals + 1 snack",
                preferredBreakfast = "Quick & high protein",
                preferredDinner = "Balanced"
            ),
            onBack = {}, onDietSelected = {}, onFoodToggle = {}, onIngredientChange = {},
            onAddIngredient = {}, onRemoveIngredient = {}, onPriorityToggle = {}, onSave = {}
        )
    }
}