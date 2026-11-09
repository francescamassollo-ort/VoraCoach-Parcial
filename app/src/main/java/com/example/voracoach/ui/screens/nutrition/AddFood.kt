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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SecondaryButton
import com.example.voracoach.ui.components.SecondaryButtonStyle
import com.example.voracoach.ui.theme.VoraCoachTheme

@Composable
fun AddFood(
    onNavigateBack: () -> Unit = {},
    onNavigateToVoice: () -> Unit = {},
    onNavigateToPhoto: () -> Unit = {},
    onNavigateToManual: () -> Unit = {},
) {
    VoraCoachTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                ScreenHeader(
                    title = "Add Food",
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
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Option 1: AI Voice Input
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToVoice,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Mic,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "AI Voice Input",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Speak naturally to log your meal...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // Option 2: Smart Photo Scan
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToPhoto,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Smart Photo Scan",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Take a picture of your plate to auto-analyze...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }

                    // Option 3: Log Manually
                    AppCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onNavigateToManual,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp),
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Log Manually",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Type in your meal, calories, and macros manually...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bottom Section: Cancel button
                SecondaryButton(
                    text = "Cancel",
                    onClick = onNavigateBack,
                    style = SecondaryButtonStyle.Outlined,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddFoodPreview() {
    VoraCoachTheme {
        AddFood()
    }
}
