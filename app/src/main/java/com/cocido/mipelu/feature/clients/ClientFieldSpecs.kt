package com.cocido.mipelu.feature.clients

import androidx.compose.ui.text.input.KeyboardType
import com.cocido.mipelu.domain.model.Client

/**
 * Descriptor de campo dinámico: permite renderizar los formularios de
 * clienta (alta rápida y ficha técnica completa) recorriendo una lista,
 * igual que los `sc-for` del diseño, en vez de repetir un TextField por campo.
 */
data class ClientFieldSpec(
    val label: String,
    val placeholder: String = "",
    val multiline: Boolean = false,
    val getter: (Client) -> String,
    val setter: (Client, String) -> Client,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val suggestions: List<String> = emptyList(),
)

val HairTypeSuggestions = listOf("Liso", "Ondulado", "Rizado", "Afro")
val PorositySuggestions = listOf("Baja", "Media", "Alta")
val SensitivitySuggestions = listOf("Ninguna", "Baja", "Media", "Alta")

/** Alta rápida de clienta (7 campos, pantalla "Nueva clienta"). */
val nuevaClientaFields = listOf(
    ClientFieldSpec("Nombre", "Nombre completo", false, { it.name }, { c, v -> c.copy(name = v) }),
    ClientFieldSpec(
        "Teléfono",
        "+54 9 11 1234-5678",
        false,
        { it.phone },
        { c, v -> c.copy(phone = v) },
        keyboardType = KeyboardType.Phone,
    ),
    ClientFieldSpec(
        "Tipo de cabello",
        "Ej: ondulado, grueso",
        false,
        { it.hairType },
        { c, v -> c.copy(hairType = v) },
        suggestions = HairTypeSuggestions,
    ),
    ClientFieldSpec("Color base", "Ej: castaño natural nivel 5", false, { it.baseColor }, { c, v -> c.copy(baseColor = v) }),
    ClientFieldSpec("Preferencias", "Tonos y técnicas que le gustan", true, { it.preferences }, { c, v -> c.copy(preferences = v) }),
    ClientFieldSpec("Alergias o sensibilidad", "Ej: alergia a la PPD", true, { it.allergies }, { c, v -> c.copy(allergies = v) }),
    ClientFieldSpec("Notas importantes", "Lo que no querés olvidar", true, { it.importantNotes }, { c, v -> c.copy(importantNotes = v) }),
)

/** Ficha técnica completa (9 campos, pantalla "Ficha técnica"). */
val fichaTecnicaFields = listOf(
    ClientFieldSpec(
        "Tipo de cabello",
        "",
        false,
        { it.hairType },
        { c, v -> c.copy(hairType = v) },
        suggestions = HairTypeSuggestions,
    ),
    ClientFieldSpec("Color base", "", false, { it.baseColor }, { c, v -> c.copy(baseColor = v) }),
    ClientFieldSpec(
        "Sensibilidad",
        "",
        false,
        { it.sensitivity },
        { c, v -> c.copy(sensitivity = v) },
        suggestions = SensitivitySuggestions,
    ),
    ClientFieldSpec(
        "Porosidad",
        "",
        false,
        { it.porosity },
        { c, v -> c.copy(porosity = v) },
        suggestions = PorositySuggestions,
    ),
    ClientFieldSpec("Preferencias", "", true, { it.preferences }, { c, v -> c.copy(preferences = v) }),
    ClientFieldSpec("Alergias", "", true, { it.allergies }, { c, v -> c.copy(allergies = v) }),
    ClientFieldSpec("Productos que funcionaron", "", true, { it.productsThatWorked }, { c, v -> c.copy(productsThatWorked = v) }),
    ClientFieldSpec("Productos a evitar", "", true, { it.productsToAvoid }, { c, v -> c.copy(productsToAvoid = v) }),
    ClientFieldSpec("Observaciones profesionales", "", true, { it.importantNotes }, { c, v -> c.copy(importantNotes = v) }),
)
