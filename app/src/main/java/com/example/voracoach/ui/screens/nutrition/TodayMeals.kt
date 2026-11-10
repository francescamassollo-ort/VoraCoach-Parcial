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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FreeBreakfast
import androidx.compose.material.icons.outlined.LunchDining
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.voracoach.R
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.*
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.SurfaceWhite
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionViewModel

@Composable
fun TodayMeals(
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToAddFood: () -> Unit = {},
    onNavigateToAddFoodWithCategory: (String) -> Unit = { _ -> onNavigateToAddFood() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val meals = uiState.meals
    val totalCalories = uiState.totalCalories

    var mealToDelete by remember { mutableStateOf<Meal?>(null) }

    VoraCoachTheme {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                // 1. Top Timeline Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "TIMELINE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            ),
                            color = TextSecondary,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Today's Meals",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Text(
                        text = "$totalCalories kcal",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                // 2. Timeline LazyColumn List & Inline Category Add Triggers
                val categories = listOf(
                    CategoryInfo(MealType.BREAKFAST, "BREAKFAST", "BREAKFAST", Icons.Outlined.FreeBreakfast),
                    CategoryInfo(MealType.LUNCH, "LUNCH", "LUNCH", Icons.Outlined.LunchDining),
                    CategoryInfo(MealType.AFTERNOON_SNACK, "AFTERNOON SNACK", "AFTERNOON_SNACK", Icons.Outlined.Fastfood),
                    CategoryInfo(MealType.DINNER, "DINNER", "DINNER", Icons.Outlined.DinnerDining),
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    categories.forEach { category ->
                        val categoryMeals = meals.filter { it.type == category.type }

                        item(key = category.type.name + "_header") {
                            AppSectionLabel(text = category.label)
                        }

                        if (categoryMeals.isNotEmpty()) {
                            items(categoryMeals, key = { it.id }) { meal ->
                                AppCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = meal.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                text = "P: ${meal.protein.toInt()}g · C: ${meal.carbs.toInt()}g · F: ${meal.fat.toInt()}g",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary,
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            Text(
                                                text = "${meal.calories} kcal",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                            )

                                            IconButton(
                                                onClick = { mealToDelete = meal },
                                                modifier = Modifier.size(28.dp),
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.DeleteOutline,
                                                    contentDescription = "Delete meal",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(18.dp),
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            item(key = category.type.name + "_empty") {
                                AppCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = "No items logged yet",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextMuted,
                                        )
                                        Button(
                                            onClick = { onNavigateToAddFoodWithCategory(category.categoryKey) },
                                            shape = CircleShape,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DarkGreen,
                                                contentColor = SurfaceWhite,
                                            ),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        ) {
                                            Text(
                                                text = stringResource(R.string.nutrition_add_meal),
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Alert Dialog before deletion
    mealToDelete?.let { meal ->
        AlertDialog(
            onDismissRequest = { mealToDelete = null },
            title = {
                Text(
                    text = "Delete Meal",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${meal.name}'?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeMeal(meal)
                        mealToDelete = null
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
                TextButton(onClick = { mealToDelete = null }) {
                    Text(stringResource(R.string.nutrition_cancel), color = TextSecondary)
                }
            },
        )
    }
}

private data class CategoryInfo(
    val type: MealType,
    val label: String,
    val categoryKey: String,
    val icon: ImageVector,
)

@Preview(showBackground = true)
@Composable
private fun TodayMealsPreview() {
    VoraCoachTheme {
        TodayMeals()
    }
}
