package com.example.voracoach.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voracoach.ui.theme.VoraCoachTheme // ← cambialo si tu tema se llama distinto
import com.example.voracoach.ui.theme.White12

enum class AppChipStyle {
    /** Para fondos claros: blanco con borde gris, texto de 13 sp (preguntas sugeridas, filtros). */
    Light,

    /** Para fondos verde oscuro: blanco translúcido al 12 %, texto blanco de 12 sp. */
    OnDark,

    /** Para fondos claros con selección en menta: el chip elegido es menta con texto verde oscuro (preferencia de workout, dieta). */
    Mint,
}

/**
 * Chip en forma de píldora. Sirve para preguntas sugeridas, filtros y opciones elegibles.
 *
 * @param selected estado seleccionado. ⚠ Figma muestra chips seleccionados en verde oscuro y en
 * menta, pero no pude leer los valores exactos; acá el seleccionado es verde oscuro con texto
 * blanco (sobre fondo oscuro, verde lima con texto verde oscuro). Revisalo contra el diseño.
 * @param onClick si es null el chip no es clickeable.
 */
@Composable
fun AppChip(
    text: String,
    modifier: Modifier = Modifier,
    style: AppChipStyle = AppChipStyle.Light,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val onDark = style == AppChipStyle.OnDark
    val mint = style == AppChipStyle.Mint

    val containerColor: Color = when {
        mint && selected -> scheme.secondary
        onDark && selected -> scheme.primaryContainer
        onDark -> White12
        selected -> scheme.primary
        else -> scheme.surface
    }
    val contentColor: Color = when {
        mint && selected -> scheme.onSecondary
        onDark && selected -> scheme.onPrimaryContainer
        onDark -> Color.White
        selected -> scheme.onPrimary
        else -> scheme.onSurface
    }
    val border = when {
        onDark -> null
        mint && selected -> BorderStroke(1.dp, scheme.secondary)
        selected -> BorderStroke(1.dp, scheme.primary)
        else -> BorderStroke(1.dp, scheme.outline)
    }
    val padding = if (onDark) {
        PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    } else {
        PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    }
    val textStyle = MaterialTheme.typography.bodySmall.copy(
        fontSize = if (onDark) 12.sp else 13.sp,
        fontWeight = FontWeight.Medium,
    )

    val chipModifier = modifier.semantics { this.selected = selected }
    // El Box centra el texto cuando el chip se estira (por ejemplo con weight en una fila)
    val label: @Composable () -> Unit = {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, style = textStyle, modifier = Modifier.padding(padding))
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = chipModifier,
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            content = label,
        )
    } else {
        Surface(
            modifier = chipModifier,
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            content = label,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class) // FlowRow es experimental en versiones viejas de Compose
@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Chips sobre fondo claro")
@Composable
private fun AppChipLightPreview() {
    VoraCoachTheme {
        var selected by remember { mutableStateOf("4 days") }
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            listOf("3 days", "4 days", "5 days", "6+ days").forEach { option ->
                AppChip(
                    text = option,
                    selected = option == selected,
                    onClick = { selected = option },
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Chips sobre verde oscuro")
@Composable
private fun AppChipOnDarkPreview() {
    VoraCoachTheme {
        FlowRow(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppChip("Start my workout", style = AppChipStyle.OnDark, onClick = {})
            AppChip("Log breakfast", style = AppChipStyle.OnDark, onClick = {})
            AppChip("Seleccionado", style = AppChipStyle.OnDark, selected = true, onClick = {})
        }
    }
}