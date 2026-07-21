package com.cocido.mipelu.core.util

import java.util.Locale

/**
 * Human-readable size ("128 MB", "1,2 GB"). Dividing straight to MB and truncating to Int (the
 * previous approach) showed "0 MB" for any usage under 1 MB - this picks the largest unit that
 * keeps at least one significant digit instead.
 */
fun Long.toHumanReadableSize(): String {
    val units = listOf("B", "KB", "MB", "GB")
    var value = this.toDouble()
    var unitIndex = 0
    while (value >= 1024 && unitIndex < units.lastIndex) {
        value /= 1024
        unitIndex++
    }
    val formatted = if (unitIndex == 0 || value == value.toLong().toDouble()) {
        value.toLong().toString()
    } else {
        String.format(Locale.getDefault(), "%.1f", value)
    }
    return "$formatted ${units[unitIndex]}"
}
