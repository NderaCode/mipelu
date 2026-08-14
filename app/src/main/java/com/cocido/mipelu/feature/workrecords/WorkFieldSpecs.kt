package com.cocido.mipelu.feature.workrecords

import androidx.compose.ui.text.input.KeyboardType
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.WorkRecord

/**
 * Tipos que implican una fórmula química mezclada en el momento (vs. Corte/Peinado, que no).
 * Determina si la sección "Fórmula y productos" se muestra en Nuevo trabajo.
 */
val ChemicalServiceTypes = setOf(
    ServiceType.COLOR,
    ServiceType.MECHAS,
    ServiceType.BALAYAGE,
    ServiceType.ALISADO,
    ServiceType.KERATINA,
    ServiceType.TRATAMIENTO,
)

data class WorkFieldSpec(
    val label: String,
    val placeholder: String = "",
    val multiline: Boolean = false,
    val getter: (WorkRecord) -> String,
    val setter: (WorkRecord, String) -> WorkRecord,
    val keyboardType: KeyboardType = KeyboardType.Text,
)

val diagnosticoFields = listOf(
    WorkFieldSpec("Estado del cabello", "Ej: sano, con puntas resecas", false, { it.hairCondition }, { w, v -> w.copy(hairCondition = v) }),
    WorkFieldSpec("Color base", "Ej: castaño natural nivel 5", false, { it.baseColor }, { w, v -> w.copy(baseColor = v) }),
    WorkFieldSpec("Objetivo del trabajo", "Qué buscamos lograr", true, { it.objective }, { w, v -> w.copy(objective = v) }),
    WorkFieldSpec("Notas del diagnóstico", "Otras observaciones antes de empezar", true, { it.notes }, { w, v -> w.copy(notes = v) }),
)

val formulaFields = listOf(
    WorkFieldSpec("Fórmula / mezcla", "Ej: 6.3 + 7.1", true, { it.formula }, { w, v -> w.copy(formula = v) }),
    WorkFieldSpec("Productos usados", "Marca y línea de producto", true, { it.productsUsed }, { w, v -> w.copy(productsUsed = v) }),
    WorkFieldSpec("Oxidante / volumen", "Ej: 20 vol", false, { it.oxidantVolume }, { w, v -> w.copy(oxidantVolume = v) }),
    WorkFieldSpec("Tiempo de exposición", "Ej: 35 min", false, { it.exposureTime }, { w, v -> w.copy(exposureTime = v) }),
    WorkFieldSpec("Técnica aplicada", "Ej: balayage a mano libre", false, { it.technique }, { w, v -> w.copy(technique = v) }),
)

val resultadoFields = listOf(
    WorkFieldSpec("Resultado final", "Cómo quedó el trabajo", true, { it.finalResult }, { w, v -> w.copy(finalResult = v) }),
    WorkFieldSpec(
        "Precio",
        "Ej: 20.000 (usá punto para miles, coma para decimales)",
        false,
        { it.price },
        { w, v -> w.copy(price = v) },
        keyboardType = KeyboardType.Decimal,
    ),
    WorkFieldSpec("Recomendaciones", "Cuidados para la clienta", true, { it.recommendations }, { w, v -> w.copy(recommendations = v) }),
    WorkFieldSpec("Próximo seguimiento", "Nota, no es un turno", false, { it.nextFollowUpNote }, { w, v -> w.copy(nextFollowUpNote = v) }),
)
