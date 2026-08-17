package com.cocido.mipelu.core.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.cocido.mipelu.core.theme.Charcoal
import com.cocido.mipelu.core.theme.miPeluColors
import java.io.File

/** Lanza el selector de fotos del sistema (Photo Picker, sin permisos de storage). */
@Composable
fun rememberPhotoPickerLauncher(onPicked: (String) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onPicked(it.toString()) }
    }

private fun createTempImageUri(context: android.content.Context): Uri {
    val photosDir = File(context.cacheDir, "photos").apply { mkdirs() }
    val file = File.createTempFile("capture_", ".jpg", photosDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

/**
 * Lanza la cámara del sistema, pidiendo permiso primero si hace falta. La foto se guarda en un
 * archivo temporal (cache/photos/, vía FileProvider) hasta que el ViewModel lee los bytes y los
 * sube - no hace falta borrarlo explícitamente, el sistema limpia la cache eventualmente.
 */
@Composable
fun rememberCameraLauncher(onPicked: (String) -> Unit): () -> Unit {
    val context = LocalContext.current
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) pendingUri?.let { onPicked(it.toString()) }
        pendingUri = null
    }
    val launchCamera = {
        val uri = createTempImageUri(context)
        pendingUri = uri
        takePicture.launch(uri)
    }
    val requestPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) launchCamera()
    }

    return {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (hasPermission) launchCamera() else requestPermission.launch(Manifest.permission.CAMERA)
    }
}

@Composable
private fun PhotoSlot(
    photoUrl: String?,
    placeholder: String,
    onClick: () -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 120.dp,
    uploadState: PhotoUploadState = PhotoUploadState.Idle,
) {
    Box(
        modifier = modifier
            .height(height)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUrl.isNullOrBlank()) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    Icons.Outlined.AddAPhoto,
                    contentDescription = placeholder,
                    tint = MaterialTheme.miPeluColors.textoMuted,
                )
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.miPeluColors.textoMuted,
                )
            }
        } else {
            var loadFailed by remember(photoUrl) { mutableStateOf(false) }
            if (loadFailed) {
                Icon(
                    Icons.Outlined.BrokenImage,
                    contentDescription = "No se pudo cargar la foto",
                    tint = MaterialTheme.miPeluColors.textoMuted,
                )
            } else {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = placeholder,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onError = { loadFailed = true },
                )
            }
        }
        if (uploadState == PhotoUploadState.Uploading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Charcoal.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp),
                )
            }
        } else if (uploadState == PhotoUploadState.Failed) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Filled.ErrorOutline,
                    contentDescription = "No se pudo subir la foto",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
        // Camera badge stays available even mid-upload/on failure so the user can retake without
        // first dismissing anything.
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(6.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Charcoal.copy(alpha = 0.55f))
                .clickable(onClick = onCameraClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.PhotoCamera,
                contentDescription = "Sacar foto con la cámara",
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
fun BeforeAfterPhotos(
    beforeUrl: String?,
    afterUrl: String?,
    onBeforeUrlPicked: (String) -> Unit,
    onAfterUrlPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    photoHeight: androidx.compose.ui.unit.Dp = 120.dp,
    beforeTestTag: String? = null,
    afterTestTag: String? = null,
    beforeUploadState: PhotoUploadState = PhotoUploadState.Idle,
    afterUploadState: PhotoUploadState = PhotoUploadState.Idle,
) {
    val pickBefore = rememberPhotoPickerLauncher(onBeforeUrlPicked)
    val pickAfter = rememberPhotoPickerLauncher(onAfterUrlPicked)
    val cameraBefore = rememberCameraLauncher(onBeforeUrlPicked)
    val cameraAfter = rememberCameraLauncher(onAfterUrlPicked)
    val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PhotoSlot(
            photoUrl = beforeUrl,
            placeholder = "Antes",
            onClick = { pickBefore.launch(request) },
            onCameraClick = cameraBefore,
            modifier = Modifier.weight(1f).let { if (beforeTestTag != null) it.testTag(beforeTestTag) else it },
            height = photoHeight,
            uploadState = beforeUploadState,
        )
        PhotoSlot(
            photoUrl = afterUrl,
            placeholder = "Después",
            onClick = { pickAfter.launch(request) },
            onCameraClick = cameraAfter,
            modifier = Modifier.weight(1f).let { if (afterTestTag != null) it.testTag(afterTestTag) else it },
            height = photoHeight,
            uploadState = afterUploadState,
        )
    }
}
