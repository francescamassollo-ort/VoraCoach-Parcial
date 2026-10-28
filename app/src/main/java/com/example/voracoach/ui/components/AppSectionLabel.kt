package com.example.voracoach.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.voracoach.ui.theme.TextMuted

/** Etiqueta de sección en mayúsculas y gris apagado ("TRAINING FREQUENCY"). El texto se pasa en minúsculas. */
@Composable
fun AppSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = TextMuted,
        modifier = modifier
    )
}