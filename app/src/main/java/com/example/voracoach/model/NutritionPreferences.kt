package com.example.voracoach.model

enum class DietType { EVERYTHING, VEGETARIAN, VEGAN, PESCATARIAN, OTHER }

enum class FavoriteFood { CHICKEN, EGGS, FISH, RICE, PASTA, GREEK_YOGHURT, VEGETABLES, FRUIT }

enum class NutritionPriority { HIT_PROTEIN_TARGETS, EAT_MORE_CONSISTENTLY }

// Preferencias de alimentación. Los alimentos a evitar los escribe el usuario, por eso son texto libre.
data class NutritionPreferences(
    val diet: DietType,
    val favoriteFoods: Set<FavoriteFood>,
    val avoidedFoods: List<String>,
    val priorities: Set<NutritionPriority>
)