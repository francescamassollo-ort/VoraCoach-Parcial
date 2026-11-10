package com.example.voracoach.ui.screens.nutrition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.CircleIconButtonStyle
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.Mint
import com.example.voracoach.ui.theme.White12

// Estructura de las pantallas oscuras de Nutrition (Voice Logging y Food Analysis), según Figma:
// fondo verde oscuro, título chico arriba a la izquierda (opcionalmente con un punto menta),
// botón ✕ redondo a la derecha y el contenido centrado debajo.
@Composable
internal fun NutritionDarkLayout(
    title: String,
    onClose: () -> Unit,
    showLiveDot: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inverseSurface)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 20.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showLiveDot) {
                Box(
                    Modifier
                        .size(8.dp)
                        .background(Mint, CircleShape)
                )
                Spacer(Modifier.width(10.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = if (showLiveDot) 1f else 0.6f),
                modifier = Modifier.weight(1f),
            )
            CircleIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.nutrition_close),
                onClick = onClose,
                style = CircleIconButtonStyle.OnDark,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            content = content,
        )
    }
}

// Orbe de VORA con un anillo alrededor y el ícono de onda de audio; [scale] permite animarlo
@Composable
internal fun ListeningOrb(scale: Float = 1f) {
    Box(
        modifier = Modifier
            .size(200.dp)
            .border(BorderStroke(3.dp, Mint.copy(alpha = 0.35f)), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        VoraOrb(diameter = 150.dp, modifier = Modifier.scale(scale)) {
            Icon(
                imageVector = Icons.Outlined.GraphicEq,
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}

// Tarjeta translúcida sobre el fondo oscuro (transcripción, "captured input")
@Composable
internal fun DarkInfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        color = White12,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, White12),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), content = content)
    }
}
