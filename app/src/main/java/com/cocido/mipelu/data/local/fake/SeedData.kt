package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.model.WorkRecord
import java.util.concurrent.TimeUnit

/**
 * Datos de ejemplo en memoria para poder navegar la app de punta a punta
 * sin backend. Se pierden al reiniciar el proceso (esperado en esta etapa).
 */
object SeedData {
    const val DEMO_USER_ID = "demo-user"
    const val DEMO_EMAIL = "romina@mipelu.com"
    const val DEMO_PASSWORD = "mipelu123"

    val demoProfile = UserProfile(
        id = DEMO_USER_ID,
        name = "Romina Gómez",
        email = DEMO_EMAIL,
        professionalName = "Romina Gómez — Estilista",
        plan = "free",
        storageUsedBytes = 128L * 1024 * 1024,
        storageLimitBytes = 2L * 1024 * 1024 * 1024,
    )

    val initialProfiles: Map<String, UserProfile> = mapOf(DEMO_USER_ID to demoProfile)

    private fun daysAgo(days: Long): Long =
        System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days)

    val clients: List<Client> = listOf(
        Client(
            id = "client-1",
            ownerUserId = DEMO_USER_ID,
            name = "Ana Fernández",
            phone = "+54 9 11 2345-6789",
            importantNotes = "Sensible al amoníaco: usar siempre fórmulas libres de amoníaco.",
            hairType = "Ondulado, grueso",
            baseColor = "Castaño natural nivel 5",
            sensitivity = "Alta — cuero cabelludo sensible",
            preferences = "Tonos cálidos, brillo natural",
            allergies = "Amoníaco",
            porosity = "Media",
            productsThatWorked = "Wella Color Fresh, Olaplex N°3",
            productsToAvoid = "Decolorantes con amoníaco",
        ),
        Client(
            id = "client-2",
            ownerUserId = DEMO_USER_ID,
            name = "Lucía Martínez",
            phone = "+54 9 11 8765-4321",
            importantNotes = "Le encanta el balayage, viene cada 8 semanas.",
            hairType = "Liso, fino",
            baseColor = "Rubio ceniza nivel 8",
            sensitivity = "Baja",
            preferences = "Rubios fríos, mechas finas",
            allergies = "Ninguna conocida",
            porosity = "Baja",
            productsThatWorked = "Kérastase Blond Absolu",
            productsToAvoid = "Champús con sulfatos",
        ),
        Client(
            id = "client-3",
            ownerUserId = DEMO_USER_ID,
            name = "Sofía Ibáñez",
            phone = "+54 9 11 5555-1234",
            importantNotes = "Alérgica a la PPD: hacer siempre prueba de alergia 48hs antes.",
            hairType = "Rizado, grueso",
            baseColor = "Negro natural nivel 2",
            sensitivity = "Alta — alérgica a PPD",
            preferences = "Cobertura de canas total",
            allergies = "PPD (parafenilendiamina)",
            porosity = "Alta",
            productsThatWorked = "Igora Royal sin PPD",
            productsToAvoid = "Tintes con PPD",
        ),
    )

    val works: List<WorkRecord> = listOf(
        WorkRecord(
            id = "work-1",
            ownerUserId = DEMO_USER_ID,
            clientId = "client-2",
            clientName = "Lucía Martínez",
            serviceTypes = listOf(ServiceType.BALAYAGE),
            date = daysAgo(3),
            hairCondition = "Sano, con brillo natural",
            baseColor = "Rubio ceniza nivel 8",
            objective = "Aclarar puntas y refrescar el balayage",
            formula = "Decolorante + oxidante 20 vol, matiz 9.1 + 10.1",
            productsUsed = "Wella BlondorPlex, Color Touch 9.1",
            oxidantVolume = "20 vol",
            exposureTime = "35 min",
            technique = "Balayage a mano libre",
            finalResult = "Rubio ceniza luminoso, transición suave",
            price = "$ 28.000",
            recommendations = "Usar shampoo matizador cada 2 lavados",
            nextFollowUpNote = "Retoque de raíz en 8 semanas",
        ),
        WorkRecord(
            id = "work-2",
            ownerUserId = DEMO_USER_ID,
            clientId = "client-1",
            clientName = "Ana Fernández",
            serviceTypes = listOf(ServiceType.COLOR),
            date = daysAgo(10),
            hairCondition = "Con algo de sequedad en puntas",
            baseColor = "Castaño natural nivel 5",
            objective = "Cobertura de canas y luminosidad",
            formula = "5.35 + 6.3 sin amoníaco",
            productsUsed = "Igora Vibrance",
            oxidantVolume = "10 vol",
            exposureTime = "30 min",
            technique = "Aplicación global",
            finalResult = "Castaño cálido, canas cubiertas",
            price = "$ 19.500",
            recommendations = "Tratamiento de hidratación en próxima visita",
            nextFollowUpNote = "Retoque en 5-6 semanas",
        ),
        WorkRecord(
            id = "work-3",
            ownerUserId = DEMO_USER_ID,
            clientId = "client-3",
            clientName = "Sofía Ibáñez",
            serviceTypes = listOf(ServiceType.KERATINA),
            date = daysAgo(18),
            hairCondition = "Frizz alto, rulos definidos",
            baseColor = "Negro natural nivel 2",
            objective = "Reducir frizz y facilitar el peinado diario",
            formula = "Keratina sin formol",
            productsUsed = "Inoar G-Hair",
            oxidantVolume = "—",
            exposureTime = "40 min + planchado",
            technique = "Alisado progresivo",
            finalResult = "Cabello liso, brillante, sin frizz",
            price = "$ 35.000",
            recommendations = "Shampoo sin sal por 72hs",
            nextFollowUpNote = "Evaluar mantenimiento en 3 meses",
        ),
    )
}
