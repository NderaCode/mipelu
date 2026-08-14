package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.WorkRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface WorkRecordRepository {
    fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>>
    fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>>
    fun observeWork(workId: String): Flow<WorkRecord?>
    suspend fun upsertWork(work: WorkRecord): WorkRecord
    suspend fun deleteWork(workId: String)

    /** True while the first fetch of the list is in flight, so screens can tell "still loading"
     * apart from "genuinely empty" and avoid flashing an empty state on cold start. */
    val isLoading: StateFlow<Boolean>

    /** Non-null when the last fetch failed (e.g. offline). Cleared on the next successful fetch. */
    val error: StateFlow<String?>

    /** Re-fetches from the backend even if already loaded once. For pull-to-refresh. */
    suspend fun refresh(ownerUserId: String)

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
