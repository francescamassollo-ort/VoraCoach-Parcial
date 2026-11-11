package com.example.voracoach.di

import android.content.Context
import androidx.room.Room
import com.example.voracoach.data.local.AppDatabase
import com.example.voracoach.data.local.MealDao
import com.example.voracoach.data.local.SavedMealDao
import com.example.voracoach.data.local.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Módulo de Hilt para Room: crea la base de datos una sola vez y provee cada DAO.
// Los repositorios piden el DAO en su constructor y Hilt se lo entrega desde acá.
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // @ApplicationContext, Hilt entrega el contexto de la app (no el de una pantalla)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "vora.db")
            // Si cambia la estructura de la base (se sube la version) y no hay una migración escrita, Room borra y recrea las tablas en vez de cerrar la app con un error.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao = database.workoutDao()

    @Provides
    fun provideMealDao(database: AppDatabase): MealDao = database.mealDao()

    @Provides
    fun provideSavedMealDao(database: AppDatabase): SavedMealDao = database.savedMealDao()
}
