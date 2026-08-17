package com.cocido.mipelu.core.ui.components

/** Per-slot upload progress for the "Fotos" section of a work record, so each PhotoSlot can show
 * its own spinner instead of one blanket app-wide "Guardando..." covering both uploads. */
enum class PhotoUploadState { Idle, Uploading, Done, Failed }
