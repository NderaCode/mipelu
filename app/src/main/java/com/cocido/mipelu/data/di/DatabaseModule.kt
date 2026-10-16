package com.cocido.mipelu.data.di

import android.content.Context
import androidx.room.Room
import com.cocido.mipelu.data.local.ClientLocalStore
import com.cocido.mipelu.data.local.RoomClientLocalStore
import com.cocido.mipelu.data.local.RoomUserProfileLocalStore
import com.cocido.mipelu.data.local.RoomWorkRecordLocalStore
import com.cocido.mipelu.data.local.UserProfileLocalStore
import com.cocido.mipelu.data.local.WorkRecordLocalStore
import com.cocido.mipelu.data.local.db.ClientDao
import com.cocido.mipelu.data.local.db.MiPeluDatabase
import com.cocido.mipelu.data.local.db.UserProfileDao
import com.cocido.mipelu.data.local.db.WorkRecordDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MiPeluDatabase =
        Room.databaseBuilder(context, MiPeluDatabase::class.java, "mipelu.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideClientDao(db: MiPeluDatabase): ClientDao = db.clientDao()

    @Provides
    fun provideWorkRecordDao(db: MiPeluDatabase): WorkRecordDao = db.workRecordDao()

    @Provides
    fun provideUserProfileDao(db: MiPeluDatabase): UserProfileDao = db.userProfileDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalStoreModule {

    @Binds
    abstract fun bindClientLocalStore(impl: RoomClientLocalStore): ClientLocalStore

    @Binds
    abstract fun bindWorkRecordLocalStore(impl: RoomWorkRecordLocalStore): WorkRecordLocalStore

    @Binds
    abstract fun bindUserProfileLocalStore(impl: RoomUserProfileLocalStore): UserProfileLocalStore
}
