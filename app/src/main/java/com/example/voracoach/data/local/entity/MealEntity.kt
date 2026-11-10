package com.example.voracoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealType

// Tabla de Room para almacenar el registro de comidas en la base de datos local.
@Entity(tableName = "meals_table")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,
    val type: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)

// --- Conversiones modelo -> Entity (para guardar) ---

fun Meal.toEntity(): MealEntity = MealEntity(
    id = id,
    name = name,
    date = date,
    type = type.name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)

// --- Conversiones Entity -> modelo (para mostrar) ---

fun MealEntity.toModel(): Meal = Meal(
    id = id,
    name = name,
    date = date,
    type = type.toMealType(),
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)

private fun String.toMealType(): MealType =
    MealType.entries.firstOrNull { it.name == this } ?: MealType.BREAKFAST
