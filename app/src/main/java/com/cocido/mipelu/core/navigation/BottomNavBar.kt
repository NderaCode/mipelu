package com.cocido.mipelu.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

private data class BottomNavTab(
    val destination: MiPeluDestination,
    val label: String,
    val icon: ImageVector,
)

private val bottomNavTabs = listOf(
    BottomNavTab(MiPeluDestination.Home, "Inicio", Icons.Filled.Home),
    BottomNavTab(MiPeluDestination.ClientList, "Clientas", Icons.Filled.People),
    BottomNavTab(MiPeluDestination.WorkList, "Trabajos", Icons.Filled.Work),
    BottomNavTab(MiPeluDestination.Settings, "Más", Icons.Filled.MoreHoriz),
)

@Composable
fun MiPeluBottomNavBar(
    currentDestination: MiPeluDestination,
    onTabSelected: (MiPeluDestination) -> Unit,
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        bottomNavTabs.forEach { tab ->
            val selected = currentDestination == tab.destination
            NavigationBarItem(
                selected = selected,
                onClick = { onTabSelected(tab.destination) },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = Color.Transparent,
                ),
            )
        }
    }
}
