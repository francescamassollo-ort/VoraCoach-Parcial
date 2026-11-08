package com.example.voracoach.data.repository

import com.example.voracoach.data.local.MealDao
import com.example.voracoach.data.local.entity.toEntity
import com.example.voracoach.data.local.entity.toModel
import com.example.voracoach.model.Meal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepository @Inject constructor(
    private val mealDao: MealDao,
) {

    fun getMealsByDate(date: String): Flow<List<Meal>> =
        mealDao.getMealsByDate(date).map { entities ->
            entities.map { it.toModel() }
        }

    suspend fun insertMeal(meal: Meal) {
        mealDao.insertMeal(meal.toEntity())
    }

    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }
}
