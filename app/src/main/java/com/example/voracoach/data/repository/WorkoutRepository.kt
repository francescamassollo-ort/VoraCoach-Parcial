package com.example.voracoach.data.repository

import com.example.voracoach.data.local.WorkoutDao
import com.example.voracoach.data.local.entity.toEntity
import com.example.voracoach.data.local.entity.toExerciseEntities
import com.example.voracoach.data.local.entity.toModel
import com.example.voracoach.data.local.entity.toSummary
import com.example.voracoach.data.remote.WorkoutApi
import com.example.voracoach.data.remote.dto.toModel
import com.example.voracoach.model.Workout
import com.example.voracoach.model.WorkoutSummary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Repositorio de entrenamientos, con enfoque offline-first:
// - Room es la única fuente de verdad: la UI siempre lee de la base (observe...).
// - La API solo sirve para actualizar la base (refresh...). Cuando se guarda algo nuevo,
//   Room avisa por el Flow y la pantalla se actualiza sola.
// - Si la red falla, la base no se toca: la última caché válida se sigue mostrando (RF02, RF05).
//
// @Inject constructor: Hilt construye el repositorio solo, entregándole la API y el DAO
// que proveen NetworkModule y DatabaseModule. Ninguna pantalla crea esta clase a mano.
@Singleton
class WorkoutRepository @Inject constructor(
    private val api: WorkoutApi,
    private val dao: WorkoutDao
) {

    fun observeCatalog(): Flow<List<WorkoutSummary>> =
        dao.observeCatalog().map { entities -> entities.map { it.toSummary() } }

    // Emite null si el detalle nunca se descargó: la pantalla informa "no disponible sin conexión"
    fun observeWorkout(workoutId: String): Flow<Workout?> =
        dao.observeWorkout(workoutId).map { it?.toModel() }

    // Descarga el catálogo y lo guarda. El resultado solo informa si salió bien o el error;
    // los datos le llegan a la pantalla por observeCatalog().
    suspend fun refreshCatalog(): Result<Unit> = runRequest {
        val dtos = api.getWorkouts().data ?: throw DataException(DataError.InvalidResponse)
        // DTO -> modelo -> Entity (RF06). La posición conserva el orden que devolvió la API.
        val workouts = dtos.mapNotNull { it.toModel() }
        dao.saveCatalog(workouts.mapIndexed { index, workout -> workout.toEntity(index) })
    }

    suspend fun refreshWorkout(workoutId: String): Result<Unit> = runRequest {
        val dto = api.getWorkout(workoutId).data ?: throw DataException(DataError.InvalidResponse)
        val workout = dto.toModel() ?: throw DataException(DataError.InvalidResponse)
        dao.saveDetail(workout.toEntity(), workout.toExerciseEntities())
    }

    // Ejecuta un pedido y convierte cualquier error en un DataError.
    // CancellationException se vuelve a lanzar: indica que la coroutine se canceló (por ejemplo,
    // el usuario salió de la pantalla) y atraparla como error dejaría la cancelación a medias.
    private suspend fun runRequest(block: suspend () -> Unit): Result<Unit> =
        try {
            block()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(DataException(e.toDataError()))
        }
}
