package com.cocido.mipelu.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InputValidationTest {
    @Test
    fun `looksLikeEmail accepts a normal address and rejects obvious typos`() {
        assertTrue("romina@estudio.com".looksLikeEmail())
        assertTrue("qa@test.com".looksLikeEmail())
        assertFalse("romina@".looksLikeEmail())
        assertFalse("romina".looksLikeEmail())
        assertFalse("@estudio.com".looksLikeEmail())
        assertFalse("romina@estudio.".looksLikeEmail())
        assertFalse("a@b@c.com".looksLikeEmail())
    }
}
