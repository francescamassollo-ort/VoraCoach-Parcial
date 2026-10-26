package com.example.voracoach.data.remote

import com.example.voracoach.data.remote.dto.DataResponse
import com.example.voracoach.data.remote.dto.WorkoutDto
import com.example.voracoach.data.remote.dto.WorkoutSummaryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Interfaz de Retrofit que solo se declaran los endpoints, Retrofit genera la implementación.
// Las funciones son suspend para llamarlas desde coroutines sin bloquear la pantalla.
// Si el servidor responde con error (404, 503...), Retrofit lanza una HttpException
// que el repositorio traduce a un error de la app.
interface WorkoutApi {

    // level es opcional, con null, Retrofit no agrega el parámetro y la API devuelve todos
    @GET("v1/workouts")
    suspend fun getWorkouts(
        @Query("level") level: String? = null
    ): DataResponse<List<WorkoutSummaryDto>>

    @GET("v1/workouts/{workoutId}")
    suspend fun getWorkout(
        @Path("workoutId") workoutId: String
    ): DataResponse<WorkoutDto>
}
