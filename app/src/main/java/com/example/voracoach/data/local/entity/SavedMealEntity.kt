package com.example.voracoach.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.voracoach.model.SavedMeal

// Tabla de Room con las comidas guardadas (plantillas para registrar rápido).
@Entity(tableName = "saved_meals")
data class SavedMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)

// --- Conversiones modelo <-> Entity ---

fun SavedMeal.toEntity(): SavedMealEntity = SavedMealEntity(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)

fun SavedMealEntity.toModel(): SavedMeal = SavedMeal(
    id = id,
    name = name,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)
