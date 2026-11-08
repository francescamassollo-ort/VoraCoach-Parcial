package com.example.voracoach.ui.screens.nutrition

import java.util.Locale
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.FreeBreakfast
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.voracoach.R
import com.example.voracoach.model.MealType
import com.example.voracoach.ui.components.*
import com.example.voracoach.ui.theme.BorderGray
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.LimeGreen
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.SurfaceWhite
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60
import com.example.voracoach.viewmodel.NutritionViewModel

@Composable
fun NutritionDashboard(
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateToTodayMeals: () -> Unit = {},
    onNavigateToAddFood: () -> Unit = {},
) {
    Dashboard(
        viewModel = viewModel,
        onNavigateToTodayMeals = onNavigateToTodayMeals,
        onNavigateToAddFood = onNavigateToAddFood,
    )
}

@Composable
fun Dashboard(
    viewModel: NutritionViewModel = hiltViewModel(),
    onNavigateToTodayMeals: () -> Unit = {},
    onNavigateToAddFood: () -> Unit = {},
) {
    // 1. Collect real-time list of meals from database
    val meals by viewModel.allTasks.collectAsState()
    val currentDateText by viewModel.currentDate.collectAsState()

    // Dynamic Data Calculations (guarded against empty state)
    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.protein.toInt() }
    val totalCarbs = meals.sumOf { it.carbs.toInt() }
    val totalFat = meals.sumOf { it.fat.toInt() }

    val calorieGoal = 2100
    val proteinGoal = 140
    val carbsGoal = 220
    val fatGoal = 65

    val caloriePercent = if (calorieGoal > 0) ((totalCalories * 100) / calorieGoal).coerceIn(0, 100) else 0

    VoraCoachTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ScreenHeader(
                    title = stringResource(R.string.screen_nutrition),
                    onBackClick = {},
                    modifier = Modifier.statusBarsPadding(),
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = stringResource(R.string.nutrition_calendar_desc),
                                modifier = Modifier.size(20.dp),
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
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // 2. Top Area: Current Date Header Label
                Column {
                    Text(
                        text = if (currentDateText.isNotBlank()) currentDateText.uppercase() else stringResource(R.string.nutrition_date_default),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                        ),
                        color = TextSecondary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.screen_nutrition),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                // Balance Snapshot Summary Card (AppCard)
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        // Top Row: Calorie count & ring gauge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.nutrition_calories_consumed),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 0.5.sp,
                                    ),
                                    color = TextSecondary,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = String.format(Locale.getDefault(), "%,d", totalCalories),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontSize = 34.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = stringResource(
                                        R.string.nutrition_calories_goal,
                                        String.format(Locale.getDefault(), "%,d", calorieGoal),
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                )
                            }

                            // Circular Progress Indicator Ring Bar
                            Box(
                                modifier = Modifier.size(72.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val strokeWidth = 7.dp.toPx()
                                    drawArc(
                                        color = BorderGray,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    )
                                    val sweep = 360f * (caloriePercent / 100f)
                                    drawArc(
                                        color = DarkGreen,
                                        startAngle = -90f,
                                        sweepAngle = sweep,
                                        useCenter = false,
                                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    )
                                }
                                Text(
                                    text = "$caloriePercent%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = BorderGray,
                        )

                        // Bottom Row: Side-by-side Protein, Carbs, Fat macronutrient progress bars
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            MacroColumn(
                                label = stringResource(R.string.nutrition_protein),
                                currentGrams = totalProtein,
                                targetGrams = proteinGoal,
                                barColor = DarkGreen,
                                modifier = Modifier.weight(1f),
                            )
                            MacroColumn(
                                label = stringResource(R.string.nutrition_carbs),
                                currentGrams = totalCarbs,
                                targetGrams = carbsGoal,
                                barColor = Mint,
                                modifier = Modifier.weight(1f),
                            )
                            MacroColumn(
                                label = stringResource(R.string.nutrition_fat),
                                currentGrams = totalFat,
                                targetGrams = fatGoal,
                                barColor = LimeGreen,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Insight Hub: Custom InsightBanner
                InsightBanner(
                    message = stringResource(R.string.nutrition_insight_default),
                    modifier = Modifier.fillMaxWidth(),
                )

                // 3. Interactive Prompt Box ("Tell Vora what you ate")


                // Section Heading ("Today's Meals")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppSectionLabel(text = stringResource(R.string.nutrition_todays_meals))
                    TextButton(onClick = onNavigateToTodayMeals) {
                        Text(
                            text = stringResource(R.string.nutrition_view_history),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }

                // 4. Zero-State Meal Category Slots
                val slots = listOf(
                    SlotInfo(MealType.BREAKFAST, "Breakfast", Icons.Outlined.FreeBreakfast),
                    SlotInfo(MealType.LUNCH, "Lunch", Icons.Outlined.LunchDining),
                    SlotInfo(MealType.AFTERNOON_SNACK, "Snack", Icons.Outlined.Fastfood),
                    SlotInfo(MealType.DINNER, "Dinner", Icons.Outlined.DinnerDining),
                )

                slots.forEach { slot ->
                    val categoryMeals = meals.filter { it.type == slot.type }
                    if (categoryMeals.isNotEmpty()) {
                        categoryMeals.forEach { meal ->
                            AppListRow(
                                icon = slot.icon,
                                title = slot.title,
                                subtitle = meal.name,
                                onClick = onNavigateToTodayMeals,
                                trailing = {
                                    Text(
                                        text = "${meal.calories} kcal",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    )
                                },
                            )
                        }
                    } else {
                        // Empty slot placeholder with "+ Add" button
                        AppCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = slot.title,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(R.string.nutrition_not_logged),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextMuted,
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Button(
                                    onClick = onNavigateToAddFood,
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

                Spacer(Modifier.height(16.dp))

                AppCard(
                    modifier = Modifier.fillMaxWidth(),
                    style = AppCardStyle.Dark,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VoraOrb(diameter = 24.dp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.nutrition_tell_vora),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Mint,
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = stringResource(R.string.nutrition_ai_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = White60,
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = onNavigateToAddFood,
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LimeGreen,
                                contentColor = DarkGreen,
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Mic,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.nutrition_btn_voice),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            )
                        }

                        Surface(
                            onClick = onNavigateToAddFood,
                            shape = CircleShape,
                            color = White12,
                            contentColor = Color.White,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.nutrition_btn_photo),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                )
                            }
                        }

                        Surface(
                            onClick = onNavigateToAddFood,
                            shape = CircleShape,
                            color = White12,
                            contentColor = Color.White,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.nutrition_btn_search),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class SlotInfo(
    val type: MealType,
    val title: String,
    val icon: ImageVector,
)

@Composable
private fun MacroColumn(
    label: String,
    currentGrams: Int,
    targetGrams: Int,
    barColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)) {
                    append("${currentGrams}g")
                }
                withStyle(SpanStyle(color = TextSecondary, fontSize = 12.sp)) {
                    append("/${targetGrams}g")
                }
            },
        )
        Spacer(Modifier.height(6.dp))
        val progress = if (targetGrams > 0) (currentGrams.toFloat() / targetGrams.toFloat()).coerceIn(0f, 1f) else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = barColor,
            trackColor = BorderGray,
            strokeCap = StrokeCap.Round,
        )
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun DashboardPreview() {
    VoraCoachTheme {
        Dashboard()
    }
}
