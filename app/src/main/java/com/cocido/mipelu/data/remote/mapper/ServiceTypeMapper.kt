package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.domain.model.ServiceType

/** ServiceType.label already equals the backend's wire string ("Corte", "Color", ...) 1:1. */
fun ServiceType.toWireValue(): String = label

fun String.toServiceType(): ServiceType =
    ServiceType.entries.firstOrNull { it.label == this }
        ?: error("Unknown serviceType from backend: $this")
