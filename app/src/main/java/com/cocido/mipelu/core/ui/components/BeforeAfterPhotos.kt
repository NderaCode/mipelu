package com.cocido.mipelu.core.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.BrokenImage
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.cocido.mipelu.core.theme.miPeluColors

/** Lanza el selector de fotos del sistema (Photo Picker, sin permisos de storage). */
@Composable
fun rememberPhotoPickerLauncher(onPicked: (String) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let { onPicked(it.toString()) }
    }

@Composable
private fun PhotoSlot(
    photoUrl: String?,
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 120.dp,
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
) {
    val pickBefore = rememberPhotoPickerLauncher(onBeforeUrlPicked)
    val pickAfter = rememberPhotoPickerLauncher(onAfterUrlPicked)
    val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PhotoSlot(
            photoUrl = beforeUrl,
            placeholder = "Antes",
            onClick = { pickBefore.launch(request) },
            modifier = Modifier.weight(1f),
            height = photoHeight,
        )
        PhotoSlot(
            photoUrl = afterUrl,
            placeholder = "Después",
            onClick = { pickAfter.launch(request) },
            modifier = Modifier.weight(1f),
            height = photoHeight,
        )
    }
}
