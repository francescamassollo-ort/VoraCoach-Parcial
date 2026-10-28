package com.example.voracoach.model

enum class Gender { FEMALE, MALE, NON_BINARY, PREFER_NOT_TO_SAY }

// Datos personales del usuario (pantalla Edit Profile). Altura y peso quedan como texto
// porque se editan en un campo; la pantalla muestra la unidad (cm / kg).
data class UserProfile(
    val firstName: String,
    val lastName: String,
    val birthDate: String,
    val heightCm: String,
    val weightKg: String,
    val gender: Gender
)