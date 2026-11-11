package com.example.voracoach.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voracoach.data.local.entity.SavedMealEntity
import kotlinx.coroutines.flow.Flow

// DAO de las comidas guardadas: guardar, borrar y buscar por nombre.
@Dao
interface SavedMealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: SavedMealEntity)

    @Delete
    suspend fun delete(meal: SavedMealEntity)

    // Busca las comidas cuyo nombre contiene el texto (sin importar mayúsculas: LIKE en SQLite
    // no distingue mayúsculas en letras sin acento). Con texto vacío devuelve todas.
    // Devuelve Flow: si se guarda o borra una comida, la lista de resultados se actualiza sola.
    @Query("SELECT * FROM saved_meals WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun search(query: String): Flow<List<SavedMealEntity>>

    // Cantidad de comidas guardadas: para cargar las de ejemplo solo si la tabla está vacía
    @Query("SELECT COUNT(*) FROM saved_meals")
    suspend fun count(): Int
}
