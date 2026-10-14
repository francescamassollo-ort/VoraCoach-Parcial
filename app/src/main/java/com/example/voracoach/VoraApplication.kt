package com.example.voracoach

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// @HiltAndroidApp hace que Hilt genere el componente raíz de Dagger (SingletonComponent),
// que vive mientras viva la app. De ahí cuelgan Retrofit, la base Room y los repositorios.
// Sin esta anotación ninguna otra inyección funciona.
@HiltAndroidApp
class VoraApplication : Application()
