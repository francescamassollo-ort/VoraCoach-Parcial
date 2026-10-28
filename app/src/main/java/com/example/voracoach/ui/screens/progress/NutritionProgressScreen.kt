package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.model.Macro
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.AppSectionLabel
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NutritionProgressUiState
import com.example.voracoach.viewmodel.NutritionProgressViewModel

private fun Macro.labelRes(): Int = when (this) {
    Macro.CALORIES -> R.string.progress_macro_calories
    Macro.PROTEIN -> R.string.progress_macro_protein
    Macro.CARBS -> R.string.progress_macro_carbs
    Macro.FAT -> R.string.progress_macro_fat
}

private fun Macro.targetRes(): Int = when (this) {
    Macro.CALORIES -> R.string.progress_target_calories
    Macro.PROTEIN -> R.string.progress_target_protein
    Macro.CARBS -> R.string.progress_target_carbs
    Macro.FAT -> R.string.progress_target_fat
}

// Pantalla "Progress / 04 — Nutrition progress"
@Composable
fun NutritionProgressRoute(
    onBack: () -> Unit,
    viewModel: NutritionProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DemoSnackbarBox { showDemo ->
        NutritionProgressScreen(
            uiState = uiState,
            onBack = onBack,
            onMoreClick = showDemo,
            onPeriodSelected = viewModel::onPeriodSelected,
            onMacroSelected = viewModel::onMacroSelected
        )
    }
}

@Composable
fun NutritionProgressScreen(
    uiState: NutritionProgressUiState,
    onBack: () -> Unit,
    onMoreClick: () -> Unit,
    onPeriodSelected: (ProgressPeriod) -> Unit,
    onMacroSelected: (Macro) -> Unit
) {
    val data = uiState.data
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ProgressHeader(stringResource(R.string.screen_progress_nutrition), onBack, onMoreClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodSelector(selected = uiState.period, onSelect = onPeriodSelected)

            StatsStrip(
                listOf(
                    stringResource(R.string.progress_avg_calories) to stringResource(R.string.progress_kcal, data.avgCalories),
                    stringResource(R.string.progress_avg_protein) to stringResource(R.string.progress_grams, data.avgProteinGrams),
                    stringResource(R.string.progress_target_hit) to
                        stringResource(R.string.progress_days_of, data.daysOnTarget, data.totalDays)
                )
            )

            DailyTargetCard(uiState, onMacroSelected)

            AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VoraOrb(diameter = 32.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.progress_nutrition_analysis),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.progress_nutrition_analysis_text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }

            AppSectionLabel(stringResource(R.string.progress_food_habits))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HabitTile(stringResource(R.string.progress_frequent_lunch), data.frequentLunch, Modifier.weight(1f))
                HabitTile(stringResource(R.string.progress_top_protein), data.topProteinSource, Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HabitTile(
                    stringResource(R.string.progress_avg_breakfast),
                    stringResource(R.string.progress_kcal, data.avgBreakfastKcal),
                    Modifier.weight(1f)
                )
                HabitTile(
                    stringResource(R.string.progress_avg_dinner),
                    stringResource(R.string.progress_kcal, data.avgDinnerKcal),
                    Modifier.weight(1f)
                )
            }
        }
    }
}

// Pestañas de macros y un círculo por día de la última semana: HIT si se cumplió el objetivo, MISS si no
@Composable
private fun DailyTargetCard(uiState: NutritionProgressUiState, onMacroSelected: (Macro) -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Macro.entries.forEach { macro ->
                val selected = macro == uiState.macro
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background)
                        .clickable(role = Role.Tab) { onMacroSelected(macro) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(macro.labelRes()),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(uiState.macro.targetRes(), uiState.target),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        val days = stringArrayResource(R.array.progress_weekdays_short)
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            uiState.dailyHits.forEachIndexed { index, hit ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (hit) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.background,
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (hit) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                                CircleShape
                            )
                    ) {
                        Text(
                            text = days.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (hit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(if (hit) R.string.progress_hit else R.string.progress_miss),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (hit) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitTile(label: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun NutritionProgressPreview() {
    VoraCoachTheme {
        NutritionProgressScreen(
            uiState = NutritionProgressUiState(),
            onBack = {},
            onMoreClick = {},
            onPeriodSelected = {},
            onMacroSelected = {}
        )
    }
}
