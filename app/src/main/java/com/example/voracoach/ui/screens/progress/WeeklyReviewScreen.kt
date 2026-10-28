package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.CircleIconButtonStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60

// Pantalla "Progress / 06 — Weekly review", de fondo oscuro. Contenido de demostración:
// el mismo resumen de la semana que muestran el Dashboard y Nutrition progress.
@Composable
fun WeeklyReviewScreen(
    onBack: () -> Unit,
    onDinnerIdeasClick: () -> Unit,
    onNextWeekPlanClick: () -> Unit,
    onAskVoraClick: () -> Unit
) {
    val onDark = MaterialTheme.colorScheme.inverseOnSurface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CircleIconButton(
                icon = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.progress_back),
                onClick = onBack,
                style = CircleIconButtonStyle.OnDark
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.screen_progress_weekly_review), style = MaterialTheme.typography.titleLarge, color = onDark)
                Text(
                    text = stringResource(R.string.progress_review_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            VoraOrb(diameter = 44.dp)
        }

        // Resumen de la semana
        DarkPanel {
            Text(
                text = stringResource(R.string.progress_review_headline),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            Spacer(Modifier.height(14.dp))
            Row(Modifier.height(IntrinsicSize.Min)) {
                ReviewStat(stringResource(R.string.progress_review_workouts), stringResource(R.string.progress_review_workouts_value), Modifier.weight(1f))
                VerticalDivider(Modifier.padding(horizontal = 8.dp), color = White12)
                ReviewStat(stringResource(R.string.progress_avg_steps), stringResource(R.string.progress_review_steps_value), Modifier.weight(1f))
                VerticalDivider(Modifier.padding(horizontal = 8.dp), color = White12)
                ReviewStat(stringResource(R.string.progress_review_protein), stringResource(R.string.progress_review_protein_value), Modifier.weight(1f))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.progress_review_went_well),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            WentWellItem(stringResource(R.string.progress_review_well_1_title), stringResource(R.string.progress_review_well_1_body))
            WentWellItem(stringResource(R.string.progress_review_well_2_title), stringResource(R.string.progress_review_well_2_body))
            WentWellItem(stringResource(R.string.progress_review_well_3_title), stringResource(R.string.progress_review_well_3_body))
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.progress_review_to_improve),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primaryContainer
            )
            DarkPanel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primaryContainer)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(R.string.progress_review_improve_title), style = MaterialTheme.typography.titleMedium, color = onDark)
                }
                Spacer(Modifier.height(10.dp))
                Text(stringResource(R.string.progress_review_improve_body), style = MaterialTheme.typography.bodyMedium, color = White60)
                Spacer(Modifier.height(12.dp))
                // Lleva a la pestaña Nutrition, donde se planifican las comidas
                LinkRow(
                    text = stringResource(R.string.progress_review_dinner_ideas),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = onDinnerIdeasClick
                )
            }
        }

        // Tarjeta blanca de recomendaciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.progress_review_recommendations),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            listOf(
                R.string.progress_review_rec_1,
                R.string.progress_review_rec_2,
                R.string.progress_review_rec_3
            ).forEach { rec ->
                Row(Modifier.padding(vertical = 4.dp)) {
                    Text("•", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(rec), style = MaterialTheme.typography.bodyMedium)
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outline)
            // Abre el plan semanal de Workout (Workout / 02)
            LinkRow(
                text = stringResource(R.string.progress_review_next_week_plan),
                color = MaterialTheme.colorScheme.primary,
                onClick = onNextWeekPlanClick
            )
        }

        // "Ask VORA" abre el asistente; "Done" vuelve al Dashboard
        Button(
            onClick = onAskVoraClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Outlined.Mic, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.progress_review_ask_vora), style = MaterialTheme.typography.titleSmall)
        }
        OutlinedButton(
            onClick = onBack,
            border = BorderStroke(1.dp, White12),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = White12, contentColor = onDark),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(stringResource(R.string.progress_review_done), style = MaterialTheme.typography.titleSmall)
        }
    }
}

// Panel verde un poco más claro que el fondo, con borde blanco translúcido
@Composable
private fun DarkPanel(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(White12, shape)
            .border(1.dp, White12, shape)
            .padding(20.dp)
    ) { content() }
}

@Composable
private fun ReviewStat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = White60)
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.inverseOnSurface
        )
    }
}

@Composable
private fun WentWellItem(title: String, body: String) {
    Row {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .background(White12, CircleShape)
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.inverseOnSurface)
            Text(body, style = MaterialTheme.typography.bodySmall, color = White60)
        }
    }
}

@Composable
private fun LinkRow(text: String, color: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = color)
        Spacer(Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
    }
}

@Preview(heightDp = 1500)
@Composable
private fun WeeklyReviewPreview() {
    VoraCoachTheme {
        WeeklyReviewScreen(onBack = {}, onDinnerIdeasClick = {}, onNextWeekPlanClick = {}, onAskVoraClick = {})
    }
}
