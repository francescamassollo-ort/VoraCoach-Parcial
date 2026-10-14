// Build raíz: solo declara los plugins. "apply false" los deja disponibles
// para que cada módulo (acá solo :app) decida si los aplica.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false   // En AGP 9 ya incluye el soporte de Kotlin; no hace falta kotlin.android
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
