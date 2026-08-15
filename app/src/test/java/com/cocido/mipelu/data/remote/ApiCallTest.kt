package com.cocido.mipelu.data.remote

import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

private fun httpException(code: Int) =
    HttpException(Response.error<Any>(code, "".toResponseBody("application/json".toMediaType())))

class ApiCallTest {

    @Test
    fun `IOException maps to a connectivity message`() = runTest {
        val exception = runCatching {
            safeApiCall<Unit> { throw IOException("timeout") }
        }.exceptionOrNull() as MiPeluApiException

        assertEquals("No se pudo conectar. Revisá tu conexión a internet.", exception.message)
    }

    @Test
    fun `known HTTP status codes map to Spanish messages`() = runTest {
        val cases = mapOf(
            400 to "Los datos ingresados no son válidos.",
            401 to "Tu sesión expiró. Iniciá sesión de nuevo.",
            403 to "No tenés permiso para hacer esto.",
            404 to "No se encontró lo que buscabas.",
            409 to "Ya existe una cuenta con ese email.",
            500 to "Hubo un problema en el servidor. Probá de nuevo en un momento.",
            503 to "Hubo un problema en el servidor. Probá de nuevo en un momento.",
        )
        for ((code, expected) in cases) {
            val exception = runCatching {
                safeApiCall<Unit> { throw httpException(code) }
            }.exceptionOrNull() as MiPeluApiException
            assertEquals("status=$code", expected, exception.message)
            assertEquals(code, exception.statusCode)
        }
    }

    @Test
    fun `unknown status code falls back to a generic message`() = runTest {
        val exception = runCatching {
            safeApiCall<Unit> { throw httpException(418) }
        }.exceptionOrNull() as MiPeluApiException

        assertEquals("Ocurrió un error inesperado.", exception.message)
    }

    @Test
    fun `successful call returns the block result untouched`() = runTest {
        assertEquals("ok", safeApiCall { "ok" })
    }
}
