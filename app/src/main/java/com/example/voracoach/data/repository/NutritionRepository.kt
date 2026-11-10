package com.example.voracoach.data.repository

import com.example.voracoach.data.local.MealDao
import com.example.voracoach.data.local.SavedMealDao
import com.example.voracoach.data.local.entity.toEntity
import com.example.voracoach.data.local.entity.toModel
import com.example.voracoach.model.Meal
import com.example.voracoach.model.MealDates
import com.example.voracoach.model.MealIngredient
import com.example.voracoach.model.MealType
import com.example.voracoach.model.SavedMeal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Días de historial de ejemplo que se cargan antes de hoy
private const val HISTORY_DAYS = 13

@Singleton
class NutritionRepository @Inject constructor(
    private val mealDao: MealDao,
    private val savedMealDao: SavedMealDao,
) {

    fun getMealsByDate(date: String): Flow<List<Meal>> =
        mealDao.getMealsByDate(date).map { entities ->
            entities.map { it.toModel() }
        }

    fun observeMealsBetween(from: String, to: String): Flow<List<Meal>> =
        mealDao.observeMealsBetween(from, to).map { entities -> entities.map { it.toModel() } }

    fun observeIngredientsBetween(from: String, to: String): Flow<List<MealIngredient>> =
        mealDao.observeIngredientsBetween(from, to).map { entities -> entities.map { it.toModel() } }

    // Una comida con sus ingredientes (para el detalle). Si la comida se borra, emite null.
    fun observeMeal(mealId: Int): Flow<Meal?> =
        mealDao.observeMeal(mealId).map { it?.toModel() }

    fun observeIngredients(mealId: Int): Flow<List<MealIngredient>> =
        mealDao.observeIngredients(mealId).map { entities -> entities.map { it.toModel() } }

    suspend fun getMeal(mealId: Int): Meal? = mealDao.getMeal(mealId)?.toModel()

    suspend fun getIngredients(mealId: Int): List<MealIngredient> =
        mealDao.getIngredients(mealId).map { it.toModel() }

    // Guarda la comida y sus ingredientes juntos; devuelve el id nuevo
    suspend fun insertMeal(meal: Meal, ingredients: List<MealIngredient> = emptyList()): Int =
        mealDao.insertMealWithIngredients(meal.toEntity(), ingredients.map { it.toEntity() }).toInt()

    suspend fun updateMeal(meal: Meal, ingredients: List<MealIngredient>) {
        mealDao.updateMealWithIngredients(meal.toEntity(), ingredients.map { it.toEntity() })
    }

    // Los ingredientes se borran solos por la clave foránea con CASCADE
    suspend fun deleteMeal(meal: Meal) {
        mealDao.deleteMeal(meal.toEntity())
    }

    // Copia una comida (con sus ingredientes) a otra fecha y hora
    suspend fun duplicateMeal(mealId: Int, date: String, time: String) {
        val original = getMeal(mealId) ?: return
        insertMeal(original.copy(id = 0, date = date, time = time), getIngredients(mealId))
    }

    // La primera vez que se abre la app la tabla está vacía: se cargan comidas de ejemplo
    // (con sus ingredientes) para hoy y un historial de las 2 semanas anteriores, así el
    // Dashboard no arranca en cero y Progress / Nutrition tiene datos reales para promediar.
    suspend fun seedIfEmpty(date: String) {
        if (mealDao.countMeals() > 0) return
        demoMeals(date).forEach { (meal, ingredients) -> insertMeal(meal, ingredients) }
        for (daysAgo in 1..HISTORY_DAYS) {
            demoHistoryDay(MealDates.daysAgo(daysAgo), daysAgo)
                .forEach { (meal, ingredients) -> insertMeal(meal, ingredients) }
        }
    }

    // Un día del historial: desayuno, almuerzo y cena que van rotando según el día,
    // así los promedios y los días "en objetivo" varían.
    private fun demoHistoryDay(date: String, daysAgo: Int) = listOf(
        if (daysAgo % 2 == 0) {
            demoMeal(date, "08:15", MealType.BREAKFAST, "Oatmeal with banana", listOf(
                MealIngredient(name = "Rolled Oats", grams = 60, calories = 230, protein = 8f, carbs = 40f, fat = 4f),
                MealIngredient(name = "Banana", grams = 120, calories = 105, protein = 1f, carbs = 27f, fat = 0f),
                MealIngredient(name = "Skim Milk", grams = 150, calories = 50, protein = 5f, carbs = 7f, fat = 0f),
            ))
        } else {
            demoMeal(date, "08:40", MealType.BREAKFAST, "Scrambled eggs on toast", listOf(
                MealIngredient(name = "Eggs", grams = 120, calories = 180, protein = 15f, carbs = 1f, fat = 12f),
                MealIngredient(name = "Whole Wheat Toast", grams = 60, calories = 150, protein = 6f, carbs = 26f, fat = 2f),
            ))
        },
        if (daysAgo % 3 == 0) {
            demoMeal(date, "13:00", MealType.LUNCH, "Tuna salad", listOf(
                MealIngredient(name = "Tuna", grams = 120, calories = 160, protein = 30f, carbs = 0f, fat = 4f),
                MealIngredient(name = "Mixed Greens", grams = 100, calories = 25, protein = 2f, carbs = 5f, fat = 0f),
                MealIngredient(name = "Olive Oil", grams = 15, calories = 120, protein = 0f, carbs = 0f, fat = 14f),
            ))
        } else {
            demoMeal(date, "13:10", MealType.LUNCH, "Chicken, rice and vegetables", listOf(
                MealIngredient(name = "Grilled Chicken Breast", grams = 180, calories = 280, protein = 32f, carbs = 0f, fat = 8f),
                MealIngredient(name = "Basmati Rice", grams = 150, calories = 190, protein = 4f, carbs = 42f, fat = 1f),
                MealIngredient(name = "Mixed Steamed Vegetables", grams = 100, calories = 50, protein = 2f, carbs = 12f, fat = 5f),
            ))
        },
        if (daysAgo % 2 == 0) {
            demoMeal(date, "20:30", MealType.DINNER, "Salmon with potatoes", listOf(
                MealIngredient(name = "Salmon Fillet", grams = 170, calories = 350, protein = 34f, carbs = 0f, fat = 22f),
                MealIngredient(name = "Roasted Potatoes", grams = 200, calories = 260, protein = 5f, carbs = 50f, fat = 6f),
            ))
        } else {
            demoMeal(date, "21:00", MealType.DINNER, "Pasta with tomato sauce", listOf(
                MealIngredient(name = "Pasta", grams = 120, calories = 430, protein = 15f, carbs = 86f, fat = 2f),
                MealIngredient(name = "Tomato Sauce", grams = 150, calories = 90, protein = 3f, carbs = 14f, fat = 3f),
                MealIngredient(name = "Parmesan", grams = 15, calories = 60, protein = 5f, carbs = 0f, fat = 4f),
            ))
        },
    )

    // Arma una comida de ejemplo: los totales salen de sumar los ingredientes
    private fun demoMeal(date: String, time: String, type: MealType, name: String, ingredients: List<MealIngredient>) =
        Meal(
            name = name,
            date = date,
            time = time,
            type = type,
            calories = ingredients.sumOf { it.calories },
            protein = ingredients.sumOf { it.protein.toDouble() }.toFloat(),
            carbs = ingredients.sumOf { it.carbs.toDouble() }.toFloat(),
            fat = ingredients.sumOf { it.fat.toDouble() }.toFloat(),
        ) to ingredients

    private fun demoMeals(date: String) = listOf(
        demoMeal(date, "08:30", MealType.BREAKFAST, "Oatmeal with banana", listOf(
            MealIngredient(name = "Rolled Oats", grams = 60, calories = 230, protein = 8f, carbs = 40f, fat = 4f),
            MealIngredient(name = "Banana", grams = 120, calories = 105, protein = 1f, carbs = 27f, fat = 0f),
            MealIngredient(name = "Skim Milk", grams = 150, calories = 50, protein = 5f, carbs = 7f, fat = 0f),
        )),
        // La misma comida que muestra el Figma de Meal Details: 520 kcal, P 38 g, C 54 g, F 14 g
        demoMeal(date, "13:12", MealType.LUNCH, "Chicken, rice and vegetables", listOf(
            MealIngredient(name = "Grilled Chicken Breast", grams = 180, calories = 280, protein = 32f, carbs = 0f, fat = 8f),
            MealIngredient(name = "Basmati Rice", grams = 150, calories = 190, protein = 4f, carbs = 42f, fat = 1f),
            MealIngredient(name = "Mixed Steamed Vegetables", grams = 100, calories = 50, protein = 2f, carbs = 12f, fat = 5f),
        )),
        demoMeal(date, "17:00", MealType.AFTERNOON_SNACK, "Greek yogurt with nuts", listOf(
            MealIngredient(name = "Greek Yogurt", grams = 170, calories = 150, protein = 15f, carbs = 8f, fat = 5f),
            MealIngredient(name = "Mixed Nuts", grams = 20, calories = 120, protein = 4f, carbs = 4f, fat = 10f),
        )),
    )

    // --- Comidas guardadas ("Save without logging" y "Search Database") ---

    fun searchSavedMeals(query: String): Flow<List<SavedMeal>> =
        savedMealDao.search(query.trim()).map { entities -> entities.map { it.toModel() } }

    suspend fun saveMeal(meal: SavedMeal) {
        savedMealDao.insert(meal.toEntity())
    }

    suspend fun deleteSavedMeal(meal: SavedMeal) {
        savedMealDao.delete(meal.toEntity())
    }

    // Igual que con las comidas del día: si no hay guardadas, carga algunas de ejemplo
    // para que la búsqueda no arranque vacía.
    suspend fun seedSavedMealsIfEmpty() {
        if (savedMealDao.count() > 0) return
        demoSavedMeals().forEach { savedMealDao.insert(it.toEntity()) }
    }

    private fun demoSavedMeals() = listOf(
        SavedMeal(name = "Chicken, rice and vegetables", calories = 520, protein = 38f, carbs = 54f, fat = 14f),
        SavedMeal(name = "Protein smoothie", calories = 310, protein = 30f, carbs = 35f, fat = 6f),
        SavedMeal(name = "Avocado toast with eggs", calories = 420, protein = 20f, carbs = 32f, fat = 24f),
        SavedMeal(name = "Tuna salad", calories = 350, protein = 32f, carbs = 12f, fat = 18f),
    )
}
