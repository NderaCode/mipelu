package com.cocido.mipelu.data.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WorkRecordMapperTest {

    @Test
    fun `es-AR thousands separator is stripped`() {
        assertEquals(20000.0, "20.000".toBackendPrice())
        assertEquals(1234567.0, "1.234.567".toBackendPrice())
        assertEquals(19500.0, "$ 19.500".toBackendPrice())
    }

    @Test
    fun `single dot followed by 1-2 digits reads as a decimal point, not thousands`() {
        // Regression test: this used to silently become 205.0 (a 10x price error) because every
        // "." was treated as a thousands separator regardless of digit-grouping.
        assertEquals(20.5, "20.5".toBackendPrice())
        assertEquals(20.5, "20.50".toBackendPrice())
    }

    @Test
    fun `comma is always the decimal point`() {
        assertEquals(20.5, "20,5".toBackendPrice())
        assertEquals(20000.5, "20.000,50".toBackendPrice())
    }

    @Test
    fun `blank or non-numeric input returns null`() {
        assertNull("".toBackendPrice())
        assertNull("   ".toBackendPrice())
        assertNull("abc".toBackendPrice())
    }

    @Test
    fun `whole numbers round-trip through formatPrice`() {
        assertEquals(1200.0, "1200".toBackendPrice())
        assertEquals(0.0, "0".toBackendPrice())
    }
}
