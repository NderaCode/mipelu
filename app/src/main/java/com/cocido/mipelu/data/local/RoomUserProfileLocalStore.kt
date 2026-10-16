package com.cocido.mipelu.data.local

import com.cocido.mipelu.data.local.db.UserProfileDao
import com.cocido.mipelu.data.local.db.toDomain
import com.cocido.mipelu.data.local.db.toEntity
import com.cocido.mipelu.domain.model.UserProfile
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomUserProfileLocalStore @Inject constructor(
    private val dao: UserProfileDao,
) : UserProfileLocalStore {

    override fun observeProfile(userId: String): Flow<UserProfile?> =
        dao.observeById(userId).map { it?.toDomain() }

    override suspend fun upsert(profile: UserProfile) {
        dao.upsert(profile.toEntity())
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}
