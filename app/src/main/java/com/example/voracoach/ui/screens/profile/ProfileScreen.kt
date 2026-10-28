package com.example.voracoach.ui.screens.profile


import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.voracoach.R
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.AppBottomBar
import com.example.voracoach.ui.components.AppCardStyle
import com.example.voracoach.ui.components.AppChip
import com.example.voracoach.ui.components.AppListRow
import com.example.voracoach.ui.components.CircleIconButton
import com.example.voracoach.ui.components.PrimaryButton
import com.example.voracoach.ui.components.VoraOrb
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.ui.theme.White60
import com.example.voracoach.viewmodel.ProfileUiState
import com.example.voracoach.viewmodel.ProfileViewModel

// Pantalla "Profile / 01 — Dashboard". Perfil de demostración (sin cuentas reales).
@Composable
fun ProfileRoute(
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onConnectedDataClick: () -> Unit,
    onAccountClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        uiState = uiState,
        onSettingsClick = onSettingsClick,
        onEditProfileClick = onEditProfileClick,
        onGoalsClick = onGoalsClick,
        onPreferencesClick = onPreferencesClick,
        onConnectedDataClick = onConnectedDataClick,
        onAccountClick = onAccountClick
    )
}

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onGoalsClick: () -> Unit,
    onPreferencesClick: () -> Unit,
    onConnectedDataClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    // verticalScroll: la pantalla es más alta que el alto disponible, se desplaza (UI03)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleRow(onSettingsClick)
        ProfileCard(uiState, onEditProfileClick)
        SnapshotCard(uiState)
        PersonalisationCard(onPreferencesClick)
        AppListRow(
            icon = Icons.Outlined.GpsFixed,
            title = stringResource(R.string.profile_your_goals),
            subtitle = uiState.goal,
            onClick = onGoalsClick
        )
        AppListRow(
            icon = Icons.Outlined.Tune,
            title = stringResource(R.string.profile_your_preferences),
            subtitle = stringResource(R.string.profile_preferences_sub),
            onClick = onPreferencesClick
        )
        AppListRow(
            icon = Icons.Outlined.MonitorHeart,
            title = stringResource(R.string.profile_connected_data),
            subtitle = stringResource(R.string.profile_connected_data_sub),
            onClick = onConnectedDataClick,
            trailing = if (uiState.dataSynced) ({ SyncedBadge() }) else null
        )
        AppListRow(
            icon = Icons.Outlined.Lock,
            title = stringResource(R.string.profile_account),
            subtitle = stringResource(R.string.profile_account_sub),
            onClick = onAccountClick
        )
    }
}

@Composable
private fun TitleRow(onSettingsClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.weight(1f)
        )
        CircleIconButton(
            icon = Icons.Outlined.Settings,
            contentDescription = stringResource(R.string.profile_open_settings),
            onClick = onSettingsClick
        )
    }
}

@Composable
private fun ProfileCard(uiState: ProfileUiState, onEditProfileClick: () -> Unit) {
    AppCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(20.dp)) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.avatar),
                contentDescription = stringResource(R.string.profile_avatar_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(12.dp))
            Text(uiState.userName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = uiState.tagline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(14.dp))
            AppChip(text = stringResource(R.string.profile_edit), onClick = onEditProfileClick)
        }
    }
}

@Composable
private fun SnapshotCard(uiState: ProfileUiState) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.profile_snapshot),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        SnapshotRow(stringResource(R.string.profile_goal), uiState.goal)
        Spacer(Modifier.height(10.dp))
        SnapshotRow(stringResource(R.string.profile_experience), uiState.experience)
        Spacer(Modifier.height(10.dp))
        SnapshotRow(stringResource(R.string.profile_training), stringResource(R.string.profile_days_per_week, uiState.trainingDaysPerWeek))
        Spacer(Modifier.height(10.dp))
        SnapshotRow(stringResource(R.string.profile_preferred_workout), uiState.preferredWorkout)
    }
}

@Composable
private fun SnapshotRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(value, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PersonalisationCard(onReviewPreferences: () -> Unit) {
    // Variante oscura: el texto adentro sale blanco sin indicar color
    AppCard(modifier = Modifier.fillMaxWidth(), style = AppCardStyle.Dark, contentPadding = PaddingValues(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            VoraOrb(diameter = 32.dp)
            Spacer(Modifier.size(10.dp))
            Text(
                text = stringResource(R.string.profile_personalisation),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(stringResource(R.string.profile_knows_goals), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.profile_personalisation_hint),
            style = MaterialTheme.typography.bodySmall,
            color = White60
        )
        Spacer(Modifier.height(16.dp))
        PrimaryButton(text = stringResource(R.string.profile_review_preferences), onClick = onReviewPreferences)
    }
}

// No reutiliza StatusBadge: acá el badge es verde oscuro con texto blanco
@Composable
private fun SyncedBadge() {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Text(
            text = stringResource(R.string.profile_synced),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// La barra inferior no va dentro de ProfileScreen: AppNavHost ya la muestra en las pestañas
// y repetirla dibujaría dos. Acá se agrega solo para que el preview coincida con el mockup.
@Preview(showBackground = true, heightDp = 1180)
@Composable
private fun ProfilePreview() {
    VoraCoachTheme {
        Scaffold(bottomBar = { AppBottomBar(currentDestination = null, onItemSelected = {}) }) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                ProfileScreen(
                    uiState = ProfileUiState(
                        userName = "Alex Morgan",
                        tagline = "Building strength & improving fitness",
                        goal = "Build strength & improve fitness",
                        experience = "Intermediate",
                        trainingDaysPerWeek = 4,
                        preferredWorkout = "Strength training",
                        dataSynced = true
                    ),
                    onSettingsClick = {}, onEditProfileClick = {}, onGoalsClick = {},
                    onPreferencesClick = {}, onConnectedDataClick = {}, onAccountClick = {}
                )
            }
        }
    }
}