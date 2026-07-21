package com.cocido.mipelu.domain.model

/**
 * Tipos de trabajo cerrados que ofrece el diseño como chips seleccionables
 * (pantalla "Nuevo trabajo"). Un enum evita strings libres y da un label y
 * un badge consistente gratis en las listas y el detalle.
 */
enum class ServiceType(val label: String) {
    CORTE("Corte"),
    COLOR("Color"),
    MECHAS("Mechas"),
    BALAYAGE("Balayage"),
    ALISADO("Alisado"),
    KERATINA("Keratina"),
    TRATAMIENTO("Tratamiento"),
    PEINADO("Peinado"),
}
