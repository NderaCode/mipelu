package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.WorkRecord
import kotlinx.coroutines.flow.Flow

interface WorkRecordRepository {
    fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>>
    fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>>
    fun observeWork(workId: String): Flow<WorkRecord?>
    suspend fun upsertWork(work: WorkRecord): WorkRecord
    suspend fun deleteWork(workId: String)

    /**
     * Uploads a before/after photo for an already-saved work record (the backend requires the
     * work record to exist first: POST /work-records/:id/photos). Replaces any existing photo
     * of the same [type] for [workId]. Returns the updated [WorkRecord] with the new photo URL
     * reflected in beforePhotoUrls/afterPhotoUrls.
     */
    suspend fun uploadPhoto(
        workId: String,
        type: PhotoType,
        bytes: ByteArray,
        mimeType: String,
        fileName: String,
    ): WorkRecord

    /** Drops any cached data so the next observe() re-fetches. Call on logout. */
    fun clearCache()
}
