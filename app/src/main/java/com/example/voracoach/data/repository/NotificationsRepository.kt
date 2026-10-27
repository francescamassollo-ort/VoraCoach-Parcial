package com.example.voracoach.data.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Estado de las notificaciones de demostración, compartido entre pantallas:
// Notifications lo cambia ("Mark all as read") y Home lo observa (punto de la campana).
//
// @Singleton: Hilt crea una sola instancia para toda la app. Si cada pantalla tuviera su
// propio estado, al salir de Notifications se perdería y Home nunca se enteraría del cambio.
// Vive en memoria: al cerrar la app vuelven a estar sin leer (son notificaciones de ejemplo).
@Singleton
class NotificationsRepository @Inject constructor() {

    private val _allRead = MutableStateFlow(false)
    val allRead: StateFlow<Boolean> = _allRead.asStateFlow()

    fun markAllAsRead() {
        _allRead.value = true
    }
}
