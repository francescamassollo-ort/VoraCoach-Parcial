package com.example.voracoach.ui.screens.nutrition

import androidx.annotation.StringRes
import com.example.voracoach.R
import com.example.voracoach.model.MealType

// Nombre visible de cada tipo de comida ("Lunch"), compartido por las pantallas de Nutrition
@StringRes
internal fun MealType.labelRes(): Int = when (this) {
    MealType.BREAKFAST -> R.string.meal_type_breakfast
    MealType.LUNCH -> R.string.meal_type_lunch
    MealType.AFTERNOON_SNACK -> R.string.meal_type_snack
    MealType.DINNER -> R.string.meal_type_dinner
}
