package com.cocido.mipelu.data.di

import com.cocido.mipelu.data.local.fake.FakeAuthRepository
import com.cocido.mipelu.data.local.fake.FakeClientRepository
import com.cocido.mipelu.data.local.fake.FakeUserProfileRepository
import com.cocido.mipelu.data.local.fake.FakeWorkRecordRepository
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * UI tests need deterministic, network-free data - swaps every Remote*Repository (RepositoryModule)
 * for its Fake* counterpart (data/local/fake, otherwise unused/dead code in the real app), seeded
 * from SeedData.kt.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
abstract class TestRepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    abstract fun bindClientRepository(impl: FakeClientRepository): ClientRepository

    @Binds
    abstract fun bindWorkRecordRepository(impl: FakeWorkRecordRepository): WorkRecordRepository

    @Binds
    abstract fun bindUserProfileRepository(impl: FakeUserProfileRepository): UserProfileRepository
}
