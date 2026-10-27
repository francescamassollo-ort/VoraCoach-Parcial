package com.example.voracoach.ui.screens.home

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.voracoach.R
import com.example.voracoach.ui.components.AppCard
import com.example.voracoach.ui.components.ScreenHeader
import com.example.voracoach.ui.components.SectionHeader
import com.example.voracoach.ui.theme.VoraCoachTheme
import com.example.voracoach.viewmodel.NotificationsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

// Ícono de una notificación: uno de Material o uno exportado de Figma (res/drawable)
private sealed interface NotificationIcon {
    data class Material(val vector: ImageVector) : NotificationIcon
    data class Drawable(@DrawableRes val res: Int) : NotificationIcon
}

// Una notificación de demostración. Los textos son ids de strings.xml para no escribirlos a mano.
private data class DemoNotification(
    val icon: NotificationIcon,
    @StringRes val category: Int,
    @StringRes val time: Int,
    @StringRes val title: Int,
    @StringRes val body: Int,
    val startsUnread: Boolean
)

// La app no recibe notificaciones reales: se muestran las de Figma como demostración
private val todayNotifications = listOf(
    DemoNotification(
        NotificationIcon.Material(Icons.Outlined.AutoAwesome),
        R.string.notifications_coach_category, R.string.notifications_coach_time,
        R.string.notifications_coach_title, R.string.notifications_coach_body,
        startsUnread = true
    ),
    DemoNotification(
        NotificationIcon.Drawable(R.drawable.dumbbell),
        R.string.notifications_plan_category, R.string.notifications_plan_time,
        R.string.notifications_plan_title, R.string.notifications_plan_body,
        startsUnread = true
    ),
    DemoNotification(
        NotificationIcon.Drawable(R.drawable.apple),
        R.string.notifications_nutrition_category, R.string.notifications_nutrition_time,
        R.string.notifications_nutrition_title, R.string.notifications_nutrition_body,
        startsUnread = false
    )
)

private val yesterdayNotifications = listOf(
    DemoNotification(
        NotificationIcon.Material(Icons.Outlined.LocalFireDepartment),
        R.string.notifications_milestone_category, R.string.notifications_milestone_time,
        R.string.notifications_milestone_title, R.string.notifications_milestone_body,
        startsUnread = false
    )
)

// Pantalla "Home / 02 — Notifications" (demostración).
// El estado "todas leídas" viene del NotificationsRepository a través del ViewModel: así se
// conserva al salir de la pantalla y Home puede apagar el punto de la campana.
@Composable
fun NotificationsRoute(
    onBack: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val allRead by viewModel.allRead.collectAsStateWithLifecycle()
    NotificationsScreen(allRead = allRead, onMarkAllAsRead = viewModel::markAllAsRead, onBack = onBack)
}

@Composable
fun NotificationsScreen(
    allRead: Boolean,
    onMarkAllAsRead: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            ScreenHeader(title = "", onBackClick = onBack, modifier = Modifier.statusBarsPadding())
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.notifications_title),
                titleStyle = MaterialTheme.typography.headlineMedium,
                actionText = stringResource(R.string.notifications_mark_all_read),
                onActionClick = onMarkAllAsRead
            )

            SectionLabel(stringResource(R.string.notifications_today))
            todayNotifications.forEach { NotificationCard(it, isUnread = it.startsUnread && !allRead) }

            SectionLabel(stringResource(R.string.notifications_yesterday))
            yesterdayNotifications.forEach { NotificationCard(it, isUnread = it.startsUnread && !allRead) }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun NotificationCard(notification: DemoNotification, isUnread: Boolean) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            val iconModifier = Modifier.size(24.dp)
            when (val icon = notification.icon) {
                is NotificationIcon.Material -> Icon(icon.vector, contentDescription = null, modifier = iconModifier)
                is NotificationIcon.Drawable ->
                    Icon(ImageVector.vectorResource(icon.res), contentDescription = null, modifier = iconModifier)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(notification.category),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(notification.time),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // Punto de "no leída", con descripción para lectores de pantalla
                    if (isUnread) {
                        val unreadLabel = stringResource(R.string.notifications_unread)
                        Spacer(Modifier.width(6.dp))
                        Box(
                            Modifier
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .semantics { contentDescription = unreadLabel }
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(stringResource(notification.title), style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(notification.body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun NotificationsPreview() {
    VoraCoachTheme {
        NotificationsScreen(allRead = false, onMarkAllAsRead = {}, onBack = {})
    }
}
