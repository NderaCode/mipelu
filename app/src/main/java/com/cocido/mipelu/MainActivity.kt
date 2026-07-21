package com.cocido.mipelu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cocido.mipelu.core.navigation.MiPeluBottomNavBar
import com.cocido.mipelu.core.navigation.MiPeluDestination
import com.cocido.mipelu.core.navigation.MiPeluNavGraph
import com.cocido.mipelu.core.navigation.topLevelDestinations
import com.cocido.mipelu.core.theme.MiPeluTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiPeluTheme {
                MiPeluApp()
            }
        }
    }
}

@Composable
private fun MiPeluApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    val currentTopLevel = topLevelDestinations.find { dest ->
        currentDestination?.hierarchy?.any { it.hasRoute(dest::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (currentTopLevel != null) {
                MiPeluBottomNavBar(
                    currentDestination = currentTopLevel,
                    onTabSelected = { destination ->
                        navController.navigate(destination) {
                            popUpTo(MiPeluDestination.Home) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
        floatingActionButton = {
            val fabAction: (() -> Unit)? = when (currentTopLevel) {
                MiPeluDestination.ClientList -> ({ navController.navigate(MiPeluDestination.NewClient) })
                MiPeluDestination.WorkList -> ({ navController.navigate(MiPeluDestination.NewWork()) })
                else -> null
            }
            if (fabAction != null) {
                FloatingActionButton(
                    onClick = fabAction,
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar")
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())) {
            MiPeluNavGraph(navController = navController)
        }
    }
}
