package com.example.voracoach.model

// Comida guardada en el celu para volver a registrarla después ("Save without logging").
// No tiene fecha ni horario: es una plantilla. Al registrarla se convierte en un Meal del día.
data class SavedMeal(
    val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)
