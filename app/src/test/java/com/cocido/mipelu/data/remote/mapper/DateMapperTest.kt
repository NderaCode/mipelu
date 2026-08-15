package com.cocido.mipelu.data.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Test

class DateMapperTest {

    @Test
    fun `epoch millis round-trips through toIsoDate`() {
        // 2026-07-20T00:00:00Z
        val millis = 1784505600000L
        assertEquals("2026-07-20", millis.toIsoDate())
    }

    @Test
    fun `date-only ISO string parses to UTC midnight millis`() {
        assertEquals(1784505600000L, "2026-07-20".isoDateToMillis())
    }

    @Test
    fun `full ISO datetime string parses to the exact instant`() {
        assertEquals(1784531073123L, "2026-07-20T07:04:33.123Z".isoDateTimeToMillis())
    }
}
