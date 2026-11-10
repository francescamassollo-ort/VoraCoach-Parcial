package com.example.voracoach.model

// Tipo de comida según la hora del día o categoría.
enum class MealType {
    BREAKFAST,
    LUNCH,
    AFTERNOON_SNACK,
    DINNER;

    companion object {
        // Tipo de comida según la hora actual: se usa cuando se carga una comida sin elegir
        // el horario (desde voz o foto). Antes de las 11 desayuno, hasta las 15 almuerzo,
        // hasta las 19 merienda y después cena.
        fun forHour(hour: Int): MealType = when {
            hour < 11 -> BREAKFAST
            hour < 15 -> LUNCH
            hour < 19 -> AFTERNOON_SNACK
            else -> DINNER
        }

        fun forCurrentTime(): MealType =
            forHour(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY))
    }
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
