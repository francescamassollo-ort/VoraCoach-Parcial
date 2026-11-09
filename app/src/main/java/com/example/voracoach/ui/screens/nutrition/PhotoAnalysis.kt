package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppChipStyle
import com.example.voracoach.ui.components.InsightBanner
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.theme.TextMuted
import com.example.voracoach.ui.theme.TextSecondary
import com.example.voracoach.ui.theme.VoraCoachTheme

@Composable
fun PhotoAnalysis(
    onNavigateBack: () -> Unit = {},
    onReviewAndAdd: () -> Unit = {},
) {
    VoraCoachTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ScreenHeader(
                    title = "Photo Analysis",
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    // Central Bounding Box Simulation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        // Background Photo Placeholder
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoCamera,
                                contentDescription = "Captured Food Photo",
                                tint = TextMuted,
                                modifier = Modifier.size(56.dp),
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Analyzed Plate Photo",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                            )
                        }

                        // Bounding Overlay Chips
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 20.dp, top = 28.dp),
                        ) {
                            AppChip(
                                text = "Grilled Chicken — 250g",
                                style = AppChipStyle.Light,
                                selected = true,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 20.dp, top = 12.dp),
                        ) {
                            AppChip(
                                text = "Brown Rice — 150g",
                                style = AppChipStyle.Light,
                                selected = true,
                            )
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 24.dp, bottom = 24.dp),
                        ) {
                            AppChip(
                                text = "Steamed Veggies — 100g",
                                style = AppChipStyle.Light,
                                selected = true,
                            )
                        }
                    }

                    // Summary Data Callout Banner
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(
                                    text = "ESTIMATED TOTAL",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextSecondary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "Estimated: 520 kcal",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }

                            Text(
                                text = "P: 42g · C: 55g · F: 12g",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }

                    // Insight Tip
                    InsightBanner(
                        message = "Vora AI detected 3 food items from your plate. Tap 'Review & Add' to confirm.",
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Bottom Primary Action
                PrimaryButton(
                    text = "Review & Add",
                    onClick = onReviewAndAdd,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoAnalysisPreview() {
    VoraCoachTheme {
        PhotoAnalysis()
    }
}
