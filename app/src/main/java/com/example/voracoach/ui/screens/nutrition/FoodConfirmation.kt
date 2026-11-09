package com.example.voracoach.ui.screens.nutrition

import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.theme.BorderGray
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel

@Composable
fun FoodConfirmation(
    mealType: String = "BREAKFAST",
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
) {
    val currentTotalCalories = remember { mutableIntStateOf(0) }
    val currentTotalProtein = remember { mutableIntStateOf(0) }
    val currentTotalCarbs = remember { mutableIntStateOf(0) }
    val currentTotalFat = remember { mutableIntStateOf(0) }

    val formattedCategoryName = remember(mealType) {
        mealType.replace("_", " ")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Component 1: Add Ingredient Button
                SecondaryButton(
                    text = "+ Add Ingredient",
                    onClick = {},
                    style = SecondaryButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Component 2: Meal Totals Display Summary Card
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
                                text = "${currentTotalCalories.intValue} kcal",
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
                            MacroMetric(label = "Protein", value = "${currentTotalProtein.intValue}g")
                            MacroMetric(label = "Carbs", value = "${currentTotalCarbs.intValue}g")
                            MacroMetric(label = "Fat", value = "${currentTotalFat.intValue}g")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Component 3: Contextual Dynamic Action Button
                PrimaryButton(
                    text = "Add to $formattedCategoryName",
                    onClick = onSaveSuccess,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Component 4: Secondary Action Button
                SecondaryButton(
                    text = "Save without loggin",
                    onClick = {},
                    style = SecondaryButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
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
        FoodConfirmation()
    }
}
