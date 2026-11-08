package com.example.voracoach.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.BuildConfig
import com.example.voracoach.R
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.SettingsUiState
import com.example.voracoach.viewmodel.SettingsViewModel

// Filas de Settings. La pantalla avisa cuál se tocó y AppNavHost decide a dónde ir,
// así se suman destinos sin agregar un parámetro por fila.
enum class SettingsItem {
    ACCOUNT, PREMIUM, MANAGE_ACCOUNT,
    NOTIFICATIONS, UNITS, APPEARANCE,
    HEALTH_ACTIVITY,
    PRIVACY_POLICY, PERMISSIONS,
    HELP_CENTRE, CONTACT_SUPPORT
}

// Pantalla "Profile / 06 — Settings". Cuenta de demostración (sin cuentas reales).
@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onItemClick: (SettingsItem) -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(uiState = uiState, onBack = onBack, onItemClick = onItemClick, onLogout = onLogout)
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onItemClick: (SettingsItem) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenHeader(
                title = stringResource(R.string.settings_title),
                onBackClick = onBack,
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionLabel(R.string.settings_account)
            SettingsRow(SettingsItem.ACCOUNT, Icons.Outlined.Person, uiState.userName, uiState.email, onItemClick)
            SettingsRow(
                SettingsItem.PREMIUM, Icons.Outlined.StarBorder,
                stringResource(R.string.settings_premium), stringResource(R.string.settings_premium_sub), onItemClick
            )
            SettingsRow(
                SettingsItem.MANAGE_ACCOUNT, Icons.Outlined.EditNote,
                stringResource(R.string.settings_manage_account), null, onItemClick
            )

            SectionLabel(R.string.settings_app_preferences)
            SettingsRow(
                SettingsItem.NOTIFICATIONS, Icons.Outlined.Notifications,
                stringResource(R.string.settings_notifications), stringResource(R.string.settings_notifications_sub), onItemClick
            )
            SettingsRow(
                SettingsItem.UNITS, Icons.Outlined.Explore,
                stringResource(R.string.settings_units), stringResource(R.string.settings_units_sub), onItemClick
            )
            SettingsRow(
                SettingsItem.APPEARANCE, Icons.Outlined.Visibility,
                stringResource(R.string.settings_appearance), stringResource(R.string.settings_appearance_sub), onItemClick
            )

            SectionLabel(R.string.settings_connected_data)
            SettingsRow(
                SettingsItem.HEALTH_ACTIVITY, Icons.Outlined.MonitorHeart,
                stringResource(R.string.settings_health), stringResource(R.string.settings_health_sub), onItemClick
            )

            SectionLabel(R.string.settings_privacy_security)
            SettingsRow(
                SettingsItem.PRIVACY_POLICY, Icons.Outlined.Lock,
                stringResource(R.string.settings_privacy_policy), null, onItemClick
            )
            SettingsRow(
                SettingsItem.PERMISSIONS, Icons.Outlined.Shield,
                stringResource(R.string.settings_permissions), null, onItemClick
            )

            SectionLabel(R.string.settings_support)
            SettingsRow(
                SettingsItem.HELP_CENTRE, Icons.AutoMirrored.Outlined.HelpOutline,
                stringResource(R.string.settings_help), null, onItemClick
            )
            SettingsRow(
                SettingsItem.CONTACT_SUPPORT, Icons.Outlined.Mail,
                stringResource(R.string.settings_contact), null, onItemClick
            )

            Spacer(Modifier.height(6.dp))
            LogoutButton(onLogout)
            Text(
                text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SectionLabel(text: Int) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 10.dp)
    )
}

// Fila: ícono en círculo, título, subtítulo opcional y flecha. La tarjeta entera es clickeable.
@Composable
private fun SettingsRow(
    item: SettingsItem,
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onItemClick: (SettingsItem) -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onItemClick(item) },
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Botón con borde y texto rojos (acción destructiva). El rojo es el color "error" del tema.
@Composable
private fun LogoutButton(onLogout: () -> Unit) {
    val red = MaterialTheme.colorScheme.error
    Surface(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, red)
    ) {
        Text(
            text = stringResource(R.string.settings_logout),
            style = MaterialTheme.typography.labelLarge,
            color = red,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 14.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun SettingsPreview() {
    VoraCoachTheme {
        SettingsScreen(
            uiState = SettingsUiState(userName = "Alex Morgan", email = "alex@morgan.com"),
            onBack = {},
            onItemClick = {},
            onLogout = {}
        )
    }
}