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
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.voracoach.R
import com.example.voracoach.model.Meal
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
import com.example.voracoach.viewmodel.NutritionUiState
import com.example.voracoach.viewmodel.NutritionViewModel

// Route: conecta la pantalla con el ViewModel y la navegación. Es lo que usa el NavHost.
@Composable
fun DashboardRoute(
    viewModel: NutritionViewModel,
    onNavigateToTodayMeals: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    onNavigateToAddFoodWithCategory: (String) -> Unit = { _ -> onNavigateToAddFood() },
    onNavigateToVoice: () -> Unit,
    onNavigateToPhoto: () -> Unit,
    onMealClick: (mealId: Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DashboardScreen(
        uiState = uiState,
        onNavigateToTodayMeals = onNavigateToTodayMeals,
        onNavigateToAddFood = onNavigateToAddFood,
        onNavigateToAddFoodWithCategory = onNavigateToAddFoodWithCategory,
        onNavigateToVoice = onNavigateToVoice,
        onNavigateToPhoto = onNavigateToPhoto,
        onMealClick = onMealClick,
    )
}

// Screen: solo dibuja el estado que recibe y avisa los clics. No conoce al ViewModel,
// por eso se puede previsualizar con datos de ejemplo.
@Composable
fun DashboardScreen(
    uiState: NutritionUiState,
    onNavigateToTodayMeals: () -> Unit,
    onNavigateToAddFood: () -> Unit,
    onNavigateToAddFoodWithCategory: (String) -> Unit,
    onNavigateToVoice: () -> Unit,
    onNavigateToPhoto: () -> Unit,
    onMealClick: (mealId: Int) -> Unit,
) {
    val meals = uiState.meals
    val totalCalories = uiState.totalCalories
    val totalProtein = uiState.totalProtein
    val totalCarbs = uiState.totalCarbs
    val totalFat = uiState.totalFat
    val calorieGoal = uiState.calorieGoal
    val proteinGoal = uiState.proteinGoal
    val carbsGoal = uiState.carbsGoal
    val fatGoal = uiState.fatGoal
    val caloriePercent = uiState.caloriePercent
    // Fecha de hoy para el encabezado, por ejemplo "TUESDAY, 10 NOVEMBER"
    val todayLabel = remember {
        java.text.SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(java.util.Date()).uppercase()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Top Header without back navigation arrow for top-level landing tab
            ScreenHeader(
                title = stringResource(R.string.screen_nutrition),
                onBackClick = {},
                backButtonContainerColor = Color.Transparent,
                backButtonBorderColor = null,
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
            // 1. Date Header Label
            Column {
                Text(
                    text = todayLabel,
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

            // 2. Reactive Zero-State / Dynamic Balancing Panel (AppCard)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    // Top Row: Calories consumed & circular progress ring
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.nutrition_consumed_calories),
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
                                text = stringResource(R.string.nutrition_goal_kcal, String.format(Locale.getDefault(), "%,d", calorieGoal)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary,
                            )
                        }

                        // Circular Tracking Graphic Meter
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
                                text = stringResource(R.string.nutrition_percent, caloriePercent),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = BorderGray,
                    )

                    // Bottom Row: Macronutrient progress indicators
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

            // Coaching Callout (Insight Banner)
            InsightBanner(
                message = stringResource(R.string.nutrition_insight_default),
                modifier = Modifier.fillMaxWidth(),
            )


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

            // 3. Slot-Aware Category Mapping
            val slots = listOf(
                SlotInfo(MealType.BREAKFAST, stringResource(R.string.meal_type_breakfast), "BREAKFAST", Icons.Outlined.FreeBreakfast),
                SlotInfo(MealType.LUNCH, stringResource(R.string.meal_type_lunch), "LUNCH", Icons.Outlined.LunchDining),
                SlotInfo(MealType.AFTERNOON_SNACK, stringResource(R.string.meal_type_snack), "AFTERNOON_SNACK", Icons.Outlined.Fastfood),
                SlotInfo(MealType.DINNER, stringResource(R.string.meal_type_dinner), "DINNER", Icons.Outlined.DinnerDining),
            )

            slots.forEach { slot ->
                val categoryMeals = meals.filter { it.type == slot.type }
                if (categoryMeals.isNotEmpty()) {
                    categoryMeals.forEach { meal ->
                        AppListRow(
                            icon = slot.icon,
                            title = slot.title,
                            subtitle = meal.name,
                            // Tocar una comida abre su detalle
                            onClick = { onMealClick(meal.id) },
                            trailing = {
                                Text(
                                    text = stringResource(R.string.food_kcal_value, meal.calories),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                )
                            },
                        )
                    }
                } else {
                    // Empty slot layout with category-aware "+ Add" button
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToAddFoodWithCategory(slot.categoryKey) },
                    ) {
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
                                onClick = { onNavigateToAddFoodWithCategory(slot.categoryKey) },
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

            // 4. Quick Action Button Cards ("Tell Vora what you ate")
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
                    // Voice Trigger Button
                    Button(
                        onClick = onNavigateToVoice,
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

                    // Photo Trigger Button
                    Surface(
                        onClick = onNavigateToPhoto,
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

                    // Search / Manual Trigger Button
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

private data class SlotInfo(
    val type: MealType,
    val title: String,
    val categoryKey: String,
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
        DashboardScreen(
            uiState = NutritionUiState(
                meals = listOf(
                    Meal(name = "Oatmeal with banana", date = "2026-11-10", type = MealType.BREAKFAST,
                        calories = 380, protein = 14f, carbs = 62f, fat = 8f),
                    Meal(name = "Grilled chicken salad", date = "2026-11-10", type = MealType.LUNCH,
                        calories = 520, protein = 42f, carbs = 30f, fat = 22f),
                ),
                totalCalories = 900, totalProtein = 56, totalCarbs = 92, totalFat = 30,
            ),
            onNavigateToTodayMeals = {},
            onNavigateToAddFood = {},
            onNavigateToAddFoodWithCategory = {},
            onNavigateToVoice = {},
            onNavigateToPhoto = {},
            onMealClick = {},
        )
    }
}
