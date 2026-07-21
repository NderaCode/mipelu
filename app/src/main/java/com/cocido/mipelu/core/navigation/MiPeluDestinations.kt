package com.cocido.mipelu.core.navigation

import kotlinx.serialization.Serializable

sealed interface MiPeluDestination {

    @Serializable
    data object Splash : MiPeluDestination

    @Serializable
    data object Onboarding : MiPeluDestination

    @Serializable
    data object Login : MiPeluDestination

    @Serializable
    data object SignUp : MiPeluDestination

    @Serializable
    data object Home : MiPeluDestination

    @Serializable
    data object ClientList : MiPeluDestination

    @Serializable
    data object NewClient : MiPeluDestination

    @Serializable
    data class ClientProfile(val clientId: String) : MiPeluDestination

    @Serializable
    data class ClientFicha(val clientId: String) : MiPeluDestination

    @Serializable
    data object WorkList : MiPeluDestination

    @Serializable
    data class NewWork(
        val clientId: String? = null,
        val duplicateFromWorkId: String? = null,
        val editWorkId: String? = null,
    ) : MiPeluDestination

    @Serializable
    data class WorkDetail(val workId: String) : MiPeluDestination

    @Serializable
    data object Settings : MiPeluDestination
}

/** Rutas de nivel superior: muestran bottom nav + FAB, según el diseño (showBottomNav/showFab). */
val topLevelDestinations = setOf(
    MiPeluDestination.Home,
    MiPeluDestination.ClientList,
    MiPeluDestination.WorkList,
    MiPeluDestination.Settings,
)
