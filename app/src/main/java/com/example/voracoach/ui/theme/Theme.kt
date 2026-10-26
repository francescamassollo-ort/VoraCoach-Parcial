package com.example.voracoach.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)*/

private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = LimeGreen,          // botón principal
    onPrimaryContainer = DarkGreen,
    secondary = Mint,
    onSecondary = DarkGreen,
    secondaryContainer = MintTint,
    onSecondaryContainer = DarkGreen,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundLight,
    onSurfaceVariant = TextSecondary,
    outline = BorderGray,
    outlineVariant = BorderGray,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun VoraCoachTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}