package com.cocido.mipelu.core.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream

private const val MAX_DIMENSION_PX = 1600
private const val JPEG_QUALITY = 85

/**
 * Downsamples and re-encodes the photo at [uri] as JPEG, capped at [MAX_DIMENSION_PX] on its
 * long side. Bounds are decoded first so the full-resolution bitmap (40+ MB decoded on a modern
 * phone camera) is never held in memory - only the already-downsampled one is. EXIF orientation
 * is baked into the pixels before the source stream is discarded, since the re-encoded JPEG has
 * no EXIF block at all - that also strips the photo's GPS location.
 *
 * Returns null if [uri] can't be opened or decoded (e.g. permission revoked, corrupt file).
 */
fun compressImage(contentResolver: ContentResolver, uri: Uri): ByteArray? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        ?: return null
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

    val decodeOptions = BitmapFactory.Options().apply {
        inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, MAX_DIMENSION_PX)
    }
    var bitmap = contentResolver.openInputStream(uri)?.use {
        BitmapFactory.decodeStream(it, null, decodeOptions)
    } ?: return null

    val orientation = contentResolver.openInputStream(uri)?.use {
        ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } ?: ExifInterface.ORIENTATION_NORMAL
    bitmap = applyExifRotation(bitmap, orientation)
    bitmap = scaleToMaxDimension(bitmap, MAX_DIMENSION_PX)

    return ByteArrayOutputStream().use { output ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)
        output.toByteArray()
    }.also { bitmap.recycle() }
}

/** inSampleSize must be a power of two; halves it until the long side is just under [maxDimension]. */
private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
    var sampleSize = 1
    var longSide = maxOf(width, height)
    while (longSide / 2 >= maxDimension) {
        sampleSize *= 2
        longSide /= 2
    }
    return sampleSize
}

private fun applyExifRotation(bitmap: Bitmap, orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        else -> return bitmap
    }
    val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (rotated !== bitmap) bitmap.recycle()
    return rotated
}

/** inSampleSize only halves, so the decoded bitmap can still be up to ~2x over [maxDimension]. */
private fun scaleToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val longSide = maxOf(bitmap.width, bitmap.height)
    if (longSide <= maxDimension) return bitmap
    val scale = maxDimension.toFloat() / longSide
    val targetWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
    val targetHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    if (scaled !== bitmap) bitmap.recycle()
    return scaled
}
