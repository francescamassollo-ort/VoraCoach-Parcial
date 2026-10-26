package com.example.voracoach.data.repository

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.io.IOException

// Errores de datos que entiende la app. El repositorio traduce las excepciones técnicas
// (Retrofit, Gson, red) a estos casos, y la UI decide qué mensaje mostrar para cada uno.
// Es sealed: el compilador obliga a que un "when" sobre DataError contemple todos los casos.
// Cada caso corresponde a un escenario del mock (X-Mock-Scenario) para probar RF05.
sealed interface DataError {
    // Sin internet o servidor apagado: no llegó ninguna respuesta
    data object NoConnection : DataError

    // El servidor respondió con error (por ejemplo 503 con el escenario "error")
    data class Server(val httpCode: Int) : DataError

    // Llegó una respuesta, pero no se pudo leer (escenario "malformed" o falta "data")
    data object InvalidResponse : DataError

    // El recurso pedido no existe (404, por ejemplo un entrenamiento con id inexistente)
    data object NotFound : DataError

    // Cualquier otro caso no previsto
    data object Unknown : DataError
}

// Excepción que transporta un DataError. Permite que el repositorio informe el error
// con Result.failure y que quien lo reciba recupere el caso con toDataError().
class DataException(val error: DataError) : Exception(error.toString())

fun Throwable.toDataError(): DataError = when (this) {
    is DataException -> error
    is HttpException -> if (code() == 404) DataError.NotFound else DataError.Server(code())
    // MalformedJsonException hereda de IOException: se revisa antes para que un JSON roto
    // no se confunda con "sin conexión"
    is MalformedJsonException, is JsonParseException -> DataError.InvalidResponse
    is IOException -> DataError.NoConnection
    else -> DataError.Unknown
}
