package com.example.voracoach.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Objetivos diarios de demostración. Los usan Nutrition (Dashboard, Meal Details) y
// Progress (Nutrition progress), así las dos áreas muestran los mismos números.
object NutritionGoals {
    const val CALORIES = 2_100
    const val PROTEIN = 140
    const val CARBS = 220
    const val FAT = 65
}

// Fechas de las comidas, en el mismo formato que se guarda en Room ("yyyy-MM-dd").
// Se usa Calendar y no java.time porque la app soporta desde la API 24.
object MealDates {
    private const val PATTERN = "yyyy-MM-dd"

    fun today(): String = format(Calendar.getInstance())

    // Fecha de hace [days] días (0 = hoy)
    fun daysAgo(days: Int): String =
        format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -days) })

    // Los 7 días de la semana actual, de lunes a domingo
    fun currentWeek(): List<String> {
        val monday = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }
        return (0 until 7).map { offset ->
            format((monday.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, offset) })
        }
    }

    private fun format(calendar: Calendar): String =
        SimpleDateFormat(PATTERN, Locale.US).format(calendar.time)
}
