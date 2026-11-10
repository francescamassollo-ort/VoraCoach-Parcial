package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.components.StatusBadge
import com.example.voracoach.ui.components.StatusBadgeStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.DarkGreen
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme
import kotlinx.coroutines.delay

@Composable
fun FoodAnalysis(
    onAnalysisComplete: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1200L)
        currentStep = 1
        delay(1200L)
        currentStep = 2
        delay(1200L)
        currentStep = 3
        delay(600L)
        onAnalysisComplete()
    }

    VoraCoachTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ScreenHeader(
                    title = "Analyzing...",
                    onBackClick = onNavigateBack,
                    modifier = Modifier.statusBarsPadding(),
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                // Central Loading Hub
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 24.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    VoraOrb(diameter = 110.dp) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = "AI Analyzing",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp),
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = "Vora AI is analyzing your food...",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground,
                    )

                    Spacer(Modifier.height(24.dp))

                    // Milestones Checklist
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        StepRow(
                            title = "Transcribing audio...",
                            isCompleted = currentStep >= 1,
                            isLoading = currentStep == 0,
                        )
                        StepRow(
                            title = "Identifying food ingredients...",
                            isCompleted = currentStep >= 2,
                            isLoading = currentStep == 1,
                        )
                        StepRow(
                            title = "Calculating macronutrient breakdown...",
                            isCompleted = currentStep >= 3,
                            isLoading = currentStep == 2,
                        )
                    }
                }

                // Bottom Action Button: Skip to Review
                SecondaryButton(
                    text = "Skip to Review",
                    onClick = onAnalysisComplete,
                    style = SecondaryButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun StepRow(
    title: String,
    isCompleted: Boolean,
    isLoading: Boolean,
) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                when {
                    isCompleted -> Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Completed",
                        tint = DarkGreen,
                        modifier = Modifier.size(22.dp),
                    )
                    isLoading -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = DarkGreen,
                    )
                    else -> Icon(
                        imageVector = Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = "Pending",
                        tint = TextMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isLoading || isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                    ),
                    color = if (isCompleted || isLoading) MaterialTheme.colorScheme.onSurface else TextSecondary,
                )
            }

            Spacer(Modifier.width(8.dp))

            when {
                isCompleted -> StatusBadge(text = "DONE", style = StatusBadgeStyle.Mint)
                isLoading -> StatusBadge(text = "IN PROGRESS", style = StatusBadgeStyle.Mint)
                else -> StatusBadge(text = "PENDING", style = StatusBadgeStyle.Plain)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FoodAnalysisPreview() {
    VoraCoachTheme {
        FoodAnalysis()
    }
}
