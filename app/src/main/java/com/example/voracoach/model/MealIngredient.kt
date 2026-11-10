package com.example.voracoach.model

// Un ingrediente de una comida registrada (por ejemplo, "Basmati Rice, 150 g, 190 kcal").
// mealId es la comida a la que pertenece.
data class MealIngredient(
    val id: Int = 0,
    val mealId: Int = 0,
    val name: String,
    val grams: Int,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)
