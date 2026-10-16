package com.cocido.mipelu.core.util

/**
 * Lightweight email check for form UX. The server still validates; this only stops an obvious
 * typo ("romina@") from being submitted so the professional sees the problem next to the field
 * instead of a generic 400 after a round trip.
 */
fun String.looksLikeEmail(): Boolean {
    val trimmed = trim()
    val at = trimmed.indexOf('@')
    if (at <= 0 || at != trimmed.lastIndexOf('@')) return false
    val domain = trimmed.substring(at + 1)
    return domain.contains('.') && domain.first() != '.' && domain.last() != '.'
}
