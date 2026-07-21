package com.cocido.mipelu.data.remote

import java.io.IOException
import retrofit2.HttpException

class MiPeluApiException(message: String, val statusCode: Int? = null) : Exception(message)

/** Runs a Retrofit suspend call, translating HTTP/network failures into a Spanish message. */
suspend fun <T> safeApiCall(block: suspend () -> T): T {
    return try {
        block()
    } catch (e: HttpException) {
        throw MiPeluApiException(mapHttpError(e.code()), e.code())
    } catch (e: IOException) {
        throw MiPeluApiException("No se pudo conectar. Revisá tu conexión a internet.")
    }
}

private fun mapHttpError(code: Int): String = when (code) {
    400 -> "Los datos ingresados no son válidos."
    401 -> "Tu sesión expiró. Iniciá sesión de nuevo."
    403 -> "No tenés permiso para hacer esto."
    404 -> "No se encontró lo que buscabas."
    409 -> "Ya existe una cuenta con ese email."
    in 500..599 -> "Hubo un problema en el servidor. Probá de nuevo en un momento."
    else -> "Ocurrió un error inesperado."
}
