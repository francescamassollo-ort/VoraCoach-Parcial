package com.example.voracoach.model

// Tipo de comida según la hora del día o categoría.
enum class MealType {
    BREAKFAST,
    LUNCH,
    AFTERNOON_SNACK,
    DINNER
}

// Modelo de la app para representar una comida registrada.
data class Meal(
    val id: Int = 0,
    val name: String,
    val date: String, // Formato YYYY-MM-DD
    val type: MealType,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
) {
    init {
        require(name.isNotBlank()) { "Meal name cannot be empty." }
        require(calories >= 0) { "Calories cannot be negative." }
        require(protein >= 0f) { "Protein cannot be negative." }
        require(carbs >= 0f) { "Carbs cannot be negative." }
        require(fat >= 0f) { "Fat cannot be negative." }
    }

    // Regla de validación explícita para formularios o capas de negocio.
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                calories >= 0 &&
                protein >= 0f &&
                carbs >= 0f &&
                fat >= 0f
    }
}
