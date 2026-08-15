package com.cocido.mipelu.core.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cocido.mipelu.core.theme.Charcoal
import com.cocido.mipelu.core.ui.TestTags
import com.cocido.mipelu.core.ui.components.AvatarInitials

private data class BottomNavTab(
    val destination: MiPeluDestination,
    val label: String,
    val icon: ImageVector?,
    val testTag: String,
)

private val bottomNavTabs = listOf(
    BottomNavTab(MiPeluDestination.Home, "Inicio", Icons.Filled.Home, TestTags.BOTTOM_NAV_HOME),
    BottomNavTab(MiPeluDestination.ClientList, "Clientas", Icons.Filled.People, TestTags.BOTTOM_NAV_CLIENTS),
    BottomNavTab(MiPeluDestination.WorkList, "Trabajos", Icons.Filled.Work, TestTags.BOTTOM_NAV_WORKS),
    BottomNavTab(MiPeluDestination.Settings, "Perfil", icon = null, testTag = TestTags.BOTTOM_NAV_PROFILE),
)

@Composable
fun MiPeluBottomNavBar(
    currentDestination: MiPeluDestination,
    onTabSelected: (MiPeluDestination) -> Unit,
    viewModel: BottomNavViewModel = hiltViewModel(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    Column {
        // Emula la sombra "hacia arriba" del diseño (Modifier.shadow solo proyecta hacia abajo).
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Charcoal.copy(alpha = 0.05f)),
                    ),
                ),
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.drawBehind {
                drawLine(
                    color = Charcoal.copy(alpha = 0.04f),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            },
        ) {
            bottomNavTabs.forEach { tab ->
                val selected = currentDestination == tab.destination
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab.destination) },
                    modifier = Modifier.testTag(tab.testTag),
                    icon = {
                        if (tab.icon != null) {
                            Icon(tab.icon, contentDescription = tab.label)
                        } else {
                            AvatarInitials(
                                initials = currentUser?.initials ?: "?",
                                size = 24.dp,
                                textStyle = MaterialTheme.typography.labelSmall,
                                containerColor = if (selected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f)
                                },
                                contentColor = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                    },
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
}
