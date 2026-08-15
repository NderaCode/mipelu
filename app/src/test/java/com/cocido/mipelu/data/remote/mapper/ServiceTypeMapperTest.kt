package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.domain.model.ServiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ServiceTypeMapperTest {

    @Test
    fun `every ServiceType round-trips through its wire value`() {
        for (type in ServiceType.entries) {
            assertEquals(type, type.toWireValue().toServiceType())
        }
    }

    @Test
    fun `unknown wire value throws instead of silently defaulting`() {
        assertThrows(IllegalStateException::class.java) { "Rulos".toServiceType() }
    }
}
