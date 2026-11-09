package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Egg
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.AppSectionLabel
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.VoraTextField
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel

@Composable
fun FoodConfirmation(
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
) {
    var nameInput by remember { mutableStateOf("") }
    var caloriesInput by remember { mutableStateOf("0") }
    var proteinInput by remember { mutableStateOf("0") }
    var carbsInput by remember { mutableStateOf("0") }
    var fatInput by remember { mutableStateOf("0") }
    var selectedType by remember { mutableStateOf(MealType.BREAKFAST) }

    var nameError by remember { mutableStateOf(false) }
    var caloriesError by remember { mutableStateOf(false) }
    var proteinError by remember { mutableStateOf(false) }
    var carbsError by remember { mutableStateOf(false) }
    var fatError by remember { mutableStateOf(false) }

    fun validateAndSave() {
        val isNameEmpty = nameInput.isBlank()
        val parsedCal = caloriesInput.toIntOrNull()
        val isCalInvalid = parsedCal == null || parsedCal < 0

        val parsedProt = proteinInput.toFloatOrNull()
        val isProtInvalid = parsedProt == null || parsedProt < 0f

        val parsedCarbs = carbsInput.toFloatOrNull()
        val isCarbsInvalid = parsedCarbs == null || parsedCarbs < 0f

        val parsedFat = fatInput.toFloatOrNull()
        val isFatInvalid = parsedFat == null || parsedFat < 0f

        nameError = isNameEmpty
        caloriesError = isCalInvalid
        proteinError = isProtInvalid
        carbsError = isCarbsInvalid
        fatError = isFatInvalid

        if (!isNameEmpty && !isCalInvalid && !isProtInvalid && !isCarbsInvalid && !isFatInvalid) {
            viewModel.addMeal(
                name = nameInput.trim(),
                type = selectedType,
                calories = parsedCal,
                protein = parsedProt,
                carbs = parsedCarbs,
                fat = parsedFat,
            )
            onSaveSuccess()
        }
    }

    VoraCoachTheme {
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
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    // Category Selectors
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppSectionLabel(text = "MEAL CATEGORY")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            val categories = listOf(
                                MealType.BREAKFAST to "Breakfast",
                                MealType.LUNCH to "Lunch",
                                MealType.AFTERNOON_SNACK to "Afternoon Snack",
                                MealType.DINNER to "Dinner",
                            )

                            categories.forEach { (type, label) ->
                                AppChip(
                                    text = label,
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    style = AppChipStyle.Light,
                                )
                            }
                        }
                    }

                    // Form Inputs
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        VoraTextField(
                            label = "Meal Name",
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                if (nameError) nameError = false
                            },
                            placeholder = "e.g. Grilled Chicken Salad",
                            leadingIcon = Icons.Outlined.Restaurant,
                            errorMessage = if (nameError) "Meal name cannot be empty" else null,
                        )

                        VoraTextField(
                            label = "Calories (kcal)",
                            value = caloriesInput,
                            onValueChange = {
                                caloriesInput = it
                                if (caloriesError) caloriesError = false
                            },
                            placeholder = "e.g. 450",
                            leadingIcon = Icons.Outlined.Bolt,
                            keyboardType = KeyboardType.Number,
                            errorMessage = if (caloriesError) "Must be a non-negative number" else null,
                        )

                        VoraTextField(
                            label = "Protein (g)",
                            value = proteinInput,
                            onValueChange = {
                                proteinInput = it
                                if (proteinError) proteinError = false
                            },
                            placeholder = "e.g. 35",
                            leadingIcon = Icons.Outlined.Egg,
                            keyboardType = KeyboardType.Number,
                            errorMessage = if (proteinError) "Must be a non-negative number" else null,
                        )

                        VoraTextField(
                            label = "Carbs (g)",
                            value = carbsInput,
                            onValueChange = {
                                carbsInput = it
                                if (carbsError) carbsError = false
                            },
                            placeholder = "e.g. 40",
                            leadingIcon = Icons.Outlined.Grain,
                            keyboardType = KeyboardType.Number,
                            errorMessage = if (carbsError) "Must be a non-negative number" else null,
                        )

                        VoraTextField(
                            label = "Fat (g)",
                            value = fatInput,
                            onValueChange = {
                                fatInput = it
                                if (fatError) fatError = false
                            },
                            placeholder = "e.g. 12",
                            leadingIcon = Icons.Outlined.WaterDrop,
                            keyboardType = KeyboardType.Number,
                            errorMessage = if (fatError) "Must be a non-negative number" else null,
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Action Button
                PrimaryButton(
                    text = "Add Meal",
                    onClick = { validateAndSave() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodConfirmationPreview() {
    VoraCoachTheme {
        FoodConfirmation()
    }
}
