package com.cocido.mipelu.data.di

import com.cocido.mipelu.data.remote.RemoteAuthRepository
import com.cocido.mipelu.data.remote.RemoteClientRepository
import com.cocido.mipelu.data.remote.RemoteUserProfileRepository
import com.cocido.mipelu.data.remote.RemoteWorkRecordRepository
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Único punto donde se elige la implementación de cada repositorio.
 * Conectados al backend NestJS real (api-mipelu) vía los Remote*Repository en data/remote/.
 * Los Fake* en data/local/fake quedan en el código sin bindear, útiles para tests/previews.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: RemoteAuthRepository): AuthRepository

    @Binds
    abstract fun bindClientRepository(impl: RemoteClientRepository): ClientRepository

    @Binds
    abstract fun bindWorkRecordRepository(impl: RemoteWorkRecordRepository): WorkRecordRepository

    @Binds
    abstract fun bindUserProfileRepository(impl: RemoteUserProfileRepository): UserProfileRepository
}
