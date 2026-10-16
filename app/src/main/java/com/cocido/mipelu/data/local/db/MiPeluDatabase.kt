package com.cocido.mipelu.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ClientEntity::class,
        WorkRecordEntity::class,
        UserProfileEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MiPeluDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun workRecordDao(): WorkRecordDao
    abstract fun userProfileDao(): UserProfileDao
}
