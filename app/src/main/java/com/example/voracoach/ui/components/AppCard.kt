package com.example.voracoach.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme

enum class AppCardStyle {
    /** Tarjeta blanca con borde gris fino y esquinas de 16 dp (métricas, listas, ejercicios). */
    Light,

    /** Tarjeta verde oscuro sin borde y esquinas de 20 dp (panel de IA de VORA). */
    Dark,
}

/**
 * Tarjeta base de la app. Es solo un contenedor: lo que va adentro lo define cada pantalla.
 *
 * En la variante [AppCardStyle.Dark] el color de contenido pasa a blanco, así que los `Text`
 * e `Icon` que pongas adentro se ven bien sin indicar color.
 *
 * @param onClick si es null la tarjeta no es clickeable; si se pasa, se vuelve clickeable.
 * @param contentPadding espacio interno. En Figma es de 16 dp (las tarjetas de métricas
 * compactas usan 12 dp).
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    style: AppCardStyle = AppCardStyle.Light,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    val isDark = style == AppCardStyle.Dark
    val shape = RoundedCornerShape(if (isDark) 20.dp else 16.dp)
    val containerColor =
        if (isDark) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (isDark) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val border = if (isDark) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)

    val inner: @Composable () -> Unit = {
        Column(modifier = Modifier.padding(contentPadding), content = content)
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            content = inner,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            content = inner,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Tarjetas")
@Composable
private fun AppCardPreview() {
    VoraCoachTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text("Upper Body Strength", style = MaterialTheme.typography.titleMedium)
                Text(
                    "42 min • 5 exercises • Intermediate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark) {
                Text("VORA Coach", style = MaterialTheme.typography.titleSmall)
                Text(
                    "How can I support your fitness targets today?",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}