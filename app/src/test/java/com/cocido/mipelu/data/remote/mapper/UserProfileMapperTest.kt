package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.StorageUsageDto
import com.cocido.mipelu.data.remote.dto.UserProfileDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private fun userProfileDto() = UserProfileDto(
    id = "user-1",
    name = "QA Stylist",
    email = "qa@test.com",
    professionalName = "QA",
    plan = "free",
    createdAt = "2026-07-20T07:04:33.123Z",
)

class UserProfileMapperTest {

    @Test
    fun `missing storage falls back to a default limit and zero usage`() {
        val domain = userProfileDto().toDomain(storage = null)

        assertEquals(0L, domain.storageUsedBytes)
        assertEquals(1024L * 1024 * 1024, domain.storageLimitBytes)
        assertNull(domain.photoUrl)
    }

    @Test
    fun `storage usage maps through when present`() {
        val storage = StorageUsageDto(usedBytes = 500L, limitBytes = 2000L)
        val domain = userProfileDto().toDomain(storage)

        assertEquals(500L, domain.storageUsedBytes)
        assertEquals(2000L, domain.storageLimitBytes)
    }
}
