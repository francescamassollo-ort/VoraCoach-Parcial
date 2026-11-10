package com.example.voracoach.ui.screens.nutrition

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.theme.LimeGreen
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White12
import com.example.voracoach.ui.theme.White60
import kotlinx.coroutines.delay

// Pasos del análisis, en orden
private val ANALYSIS_STEPS = listOf(
    R.string.analysis_step_identifying,
    R.string.analysis_step_portions,
    R.string.analysis_step_nutrition,
)

// Tiempo que dura cada paso (simulado)
private const val STEP_MILLIS = 1_200L

// "05 — Food Analysis" según Figma. El análisis es simulado: los pasos avanzan solos y al
// terminar se abre Review Meal con los ingredientes "detectados". "Cancel analysis" vuelve atrás.
@Composable
fun FoodAnalysis(
    onAnalysisComplete: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
) {
    // Paso en curso: 0, 1 o 2; 3 = terminado
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        repeat(ANALYSIS_STEPS.size) {
            delay(STEP_MILLIS)
            currentStep++
        }
        onAnalysisComplete()
    }

    FoodAnalysisContent(
        currentStep = currentStep,
        onCancel = onNavigateBack,
    )
}

// Contenido sin la animación, para poder previsualizar cualquier paso
@Composable
private fun FoodAnalysisContent(currentStep: Int, onCancel: () -> Unit) {
    NutritionDarkLayout(
        title = stringResource(R.string.analysis_header),
        onClose = onCancel,
    ) {
        Spacer(Modifier.height(16.dp))
        ListeningOrb()
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.analysis_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.inverseOnSurface,
        )
        Spacer(Modifier.height(28.dp))

        // Lo que dijo el usuario (de ejemplo, el mismo plato que precarga Review Meal)
        DarkInfoCard {
            Text(
                text = stringResource(R.string.analysis_captured_input),
                style = MaterialTheme.typography.labelSmall,
                color = Mint,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.analysis_captured_example),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ANALYSIS_STEPS.forEachIndexed { index, labelRes ->
                StepRow(
                    labelRes = labelRes,
                    state = when {
                        index < currentStep -> StepState.DONE
                        index == currentStep -> StepState.CURRENT
                        else -> StepState.PENDING
                    },
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        TextButton(onClick = onCancel) {
            Text(
                text = stringResource(R.string.analysis_cancel),
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                color = White60,
            )
        }
    }
}

private enum class StepState { DONE, CURRENT, PENDING }

// Paso del análisis: punto de color + texto. Hecho = menta, en curso = lima y negrita,
// pendiente = apagado (como en Figma)
@Composable
private fun StepRow(@StringRes labelRes: Int, state: StepState) {
    val dotColor = when (state) {
        StepState.DONE -> Mint
        StepState.CURRENT -> LimeGreen
        StepState.PENDING -> White12
    }
    val textColor = when (state) {
        StepState.DONE -> White60
        StepState.CURRENT -> Color.White
        StepState.PENDING -> Color.White.copy(alpha = 0.35f)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(14.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (state == StepState.CURRENT) FontWeight.SemiBold else FontWeight.Normal,
            ),
            color = textColor,
        )
    }
}

@Preview(showBackground = true, heightDp = 850)
@Composable
private fun FoodAnalysisPreview() {
    VoraCoachTheme {
        FoodAnalysisContent(currentStep = 1, onCancel = {})
    }
}
