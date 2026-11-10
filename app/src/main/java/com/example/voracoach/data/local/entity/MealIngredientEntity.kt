package com.example.voracoach.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.voracoach.model.MealIngredient

// Tabla de ingredientes: cada fila pertenece a una comida de meals_table (relación uno a muchos).
// La clave foránea con CASCADE hace que, al borrar una comida, Room borre sus ingredientes solo.
// El índice en mealId acelera la búsqueda de los ingredientes de una comida.
@Entity(
    tableName = "meal_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = MealEntity::class,
            parentColumns = ["id"],
            childColumns = ["mealId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("mealId")],
)
data class MealIngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: Int,
    val name: String,
    val grams: Int,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float,
)

// --- Conversiones modelo <-> Entity ---

fun MealIngredient.toEntity(): MealIngredientEntity = MealIngredientEntity(
    id = id,
    mealId = mealId,
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)

fun MealIngredientEntity.toModel(): MealIngredient = MealIngredient(
    id = id,
    mealId = mealId,
    name = name,
    grams = grams,
    calories = calories,
    protein = protein,
    carbs = carbs,
    fat = fat,
)
