package com.example.voracoach.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.ui.theme.VoraCoachTheme

private val ButtonPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
private const val DISABLED_ALPHA = 0.4f

/**
 * Botón principal: verde lima con texto verde oscuro, forma de píldora.
 *
 * @param leadingIcon ícono opcional a la izquierda del texto (16 dp).
 * @param alignStart si es true el contenido queda pegado a la izquierda, como en
 * "Start voice session" de Figma. Usalo junto con Modifier.fillMaxWidth().
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    alignStart: Boolean = false,
) {
    val container = MaterialTheme.colorScheme.primaryContainer
    val content = MaterialTheme.colorScheme.onPrimaryContainer
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = container.copy(alpha = DISABLED_ALPHA),
            disabledContentColor = content.copy(alpha = DISABLED_ALPHA),
        ),
        contentPadding = ButtonPadding,
    ) {
        Row(
            modifier = if (alignStart) Modifier.fillMaxWidth() else Modifier,
            horizontalArrangement = if (alignStart) Arrangement.Start else Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(leadingIcon, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelMedium)
        }
    }
}

enum class SecondaryButtonStyle {
    /** Relleno verde oscuro con texto blanco ("Start Workout"). */
    Filled,

    /** Solo borde verde oscuro de 1 dp y texto verde oscuro ("Edit workout"). */
    Outlined,
}

/** Botón secundario en dos variantes: relleno verde oscuro o solo borde. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: SecondaryButtonStyle = SecondaryButtonStyle.Filled,
) {
    val primary = MaterialTheme.colorScheme.primary
    when (style) {
        SecondaryButtonStyle.Filled -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = primary.copy(alpha = DISABLED_ALPHA),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = DISABLED_ALPHA),
            ),
            contentPadding = ButtonPadding,
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }

        SecondaryButtonStyle.Outlined -> OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = primary,
                disabledContentColor = primary.copy(alpha = DISABLED_ALPHA),
            ),
            border = BorderStroke(
                width = 1.dp,
                color = if (enabled) primary else primary.copy(alpha = DISABLED_ALPHA),
            ),
            contentPadding = ButtonPadding,
        ) {
            Text(text, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF123D35, name = "Principal sobre verde oscuro")
@Composable
private fun PrimaryButtonPreview() {
    VoraCoachTheme {
        PrimaryButton(
            text = "Start voice session",
            onClick = {},
            leadingIcon = Icons.Outlined.Mic,
            alignStart = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF7F8F5, name = "Secundarios")
@Composable
private fun SecondaryButtonPreview() {
    VoraCoachTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SecondaryButton("Start Workout", onClick = {}, modifier = Modifier.fillMaxWidth())
            SecondaryButton(
                "Edit workout",
                onClick = {},
                style = SecondaryButtonStyle.Outlined,
                modifier = Modifier.fillMaxWidth(),
            )
            SecondaryButton("Deshabilitado", onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}//class Appbuttons {}