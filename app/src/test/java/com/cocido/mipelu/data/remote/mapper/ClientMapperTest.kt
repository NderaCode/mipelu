package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.ClientDetailDto
import com.cocido.mipelu.domain.model.Client
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private fun clientDetailDto() = ClientDetailDto(
    id = "client-1",
    name = "Ana Pérez",
    phone = "+54 9 11 1234-5678",
    allergies = "PPD",
    createdAt = "2026-07-20T07:04:33.123Z",
    updatedAt = "2026-07-20T07:04:33.123Z",
)

class ClientMapperTest {

    @Test
    fun `null optional fields become empty strings in the domain model`() {
        val domain = ClientDetailDto(
            id = "client-1",
            name = "Ana Pérez",
            createdAt = "2026-07-20T07:04:33.123Z",
            updatedAt = "2026-07-20T07:04:33.123Z",
        ).toDomain("owner-1")

        assertEquals("", domain.phone)
        assertEquals("", domain.allergies)
    }

    @Test
    fun `present fields map through 1_1`() {
        val domain = clientDetailDto().toDomain("owner-1")

        assertEquals("client-1", domain.id)
        assertEquals("owner-1", domain.ownerUserId)
        assertEquals("Ana Pérez", domain.name)
        assertEquals("+54 9 11 1234-5678", domain.phone)
        assertEquals("PPD", domain.allergies)
    }

    @Test
    fun `blank fields become null on the upsert request, never empty strings`() {
        val client = Client(id = "client-1", ownerUserId = "owner-1", name = "Ana", phone = "")

        assertNull(client.toUpsertRequest().phone)
    }
}
