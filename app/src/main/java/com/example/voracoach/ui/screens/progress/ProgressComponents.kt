package com.example.voracoach.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.model.ProgressPeriod
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.ScreenHeader
import kotlinx.coroutines.launch

// Piezas que comparten las pantallas de Progress (Dashboard, Workout y Nutrition progress)

private fun ProgressPeriod.labelRes(): Int = when (this) {
    ProgressPeriod.WEEK -> R.string.progress_period_week
    ProgressPeriod.MONTH -> R.string.progress_period_month
    ProgressPeriod.THREE_MONTHS -> R.string.progress_period_three_months
}

// Pestañas Week / Month / 3 Months: la elegida va en verde oscuro
@Composable
internal fun PeriodSelector(
    selected: ProgressPeriod,
    onSelect: (ProgressPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            .padding(4.dp)
    ) {
        ProgressPeriod.entries.forEach { period ->
            val isSelected = period == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                    .clickable(role = Role.Tab) { onSelect(period) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(period.labelRes()),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Encabezado de las sub-pantallas: volver, título y el botón "•••" (no tiene menú: avisa que es demostración)
@Composable
internal fun ProgressHeader(title: String, onBack: () -> Unit, onMoreClick: () -> Unit) {
    ScreenHeader(
        title = title,
        onBackClick = onBack,
        actions = {
            CircleIconButton(
                icon = Icons.Outlined.MoreHoriz,
                contentDescription = stringResource(R.string.progress_more_options),
                onClick = onMoreClick,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    )
}

// Contenedor con el aviso de "función de demostración". Le pasa al contenido la función que lo muestra.
@Composable
internal fun DemoSnackbarBox(content: @Composable (showDemo: () -> Unit) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val demoMessage = stringResource(R.string.demo_only_message)
    Box(Modifier.fillMaxSize()) {
        content { scope.launch { snackbarHostState.showSnackbar(demoMessage) } }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter).padding(16.dp))
    }
}

// Fila de 3 métricas separadas por líneas verticales ("SESSIONS | VOLUME | AVG TIME")
@Composable
internal fun StatsStrip(stats: List<Pair<String, String>>, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 14.dp)) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            stats.forEachIndexed { index, (label, value) ->
                if (index > 0) VerticalDivider(color = MaterialTheme.colorScheme.outline)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Gráfico de barras simple, sin librerías: cada barra es un Box con alto proporcional al máximo.
// barColor decide el color de cada barra (por ejemplo, resaltar las que superan un objetivo).
@Composable
internal fun SimpleBarChart(
    values: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 100.dp,
    barColor: @Composable (index: Int) -> Color = { MaterialTheme.colorScheme.primary }
) {
    val max = values.maxOrNull()?.takeIf { it > 0 } ?: 1
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        values.forEachIndexed { index, value ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Un valor 0 (día de descanso) se dibuja como una barrita gris, como en el Figma
                val isEmpty = value == 0
                Box(
                    Modifier
                        .width(28.dp)
                        .height(if (isEmpty) 6.dp else chartHeight * (value.toFloat() / max))
                        .background(
                            if (isEmpty) MaterialTheme.colorScheme.outline else barColor(index),
                            RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = labels.getOrElse(index) { "" },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Etiquetas del eje del gráfico según el período: días, semanas o meses
@Composable
internal fun periodBarLabels(period: ProgressPeriod, count: Int): List<String> {
    val all = when (period) {
        ProgressPeriod.WEEK -> stringArrayResource(R.array.progress_weekdays_short)
        ProgressPeriod.MONTH -> stringArrayResource(R.array.progress_weeks_short)
        ProgressPeriod.THREE_MONTHS -> stringArrayResource(R.array.progress_months_short)
    }
    return all.take(count)
}

// Recuadro de una métrica: etiqueta arriba, valor grande y, opcional, la mejora al lado
@Composable
internal fun StatTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    delta: String? = null
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            if (delta != null) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = delta,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}
