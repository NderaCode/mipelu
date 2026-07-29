package com.cocido.mipelu.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cocido.mipelu.feature.auth.ForgotPasswordScreen
import com.cocido.mipelu.feature.auth.LoginScreen
import com.cocido.mipelu.feature.auth.OnboardingScreen
import com.cocido.mipelu.feature.auth.SignUpScreen
import com.cocido.mipelu.feature.auth.SplashScreen
import com.cocido.mipelu.feature.clients.ClientFichaScreen
import com.cocido.mipelu.feature.clients.ClientListScreen
import com.cocido.mipelu.feature.clients.ClientProfileScreen
import com.cocido.mipelu.feature.clients.NewClientScreen
import com.cocido.mipelu.feature.home.HomeScreen
import com.cocido.mipelu.feature.settings.SettingsScreen
import com.cocido.mipelu.feature.workrecords.NewWorkScreen
import com.cocido.mipelu.feature.workrecords.WorkDetailScreen
import com.cocido.mipelu.feature.workrecords.WorkListScreen

@Composable
fun MiPeluNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = MiPeluDestination.Splash) {

        composable<MiPeluDestination.Splash> {
            SplashScreen(
                onNavigateNext = { loggedIn ->
                    val next = if (loggedIn) MiPeluDestination.Home else MiPeluDestination.Onboarding
                    navController.navigate(next) {
                        popUpTo(MiPeluDestination.Splash) { inclusive = true }
                    }
                },
            )
        }

        composable<MiPeluDestination.Onboarding> {
            OnboardingScreen(
                onCrearCuenta = { navController.navigate(MiPeluDestination.SignUp) },
                onIniciarSesion = { navController.navigate(MiPeluDestination.Login) },
            )
        }

        composable<MiPeluDestination.Login> {
            LoginScreen(
                onBack = { navController.popBackStack() },
                onLoginSuccess = {
                    navController.navigate(MiPeluDestination.Home) {
                        popUpTo(MiPeluDestination.Onboarding) { inclusive = true }
                    }
                },
                onGoToSignUp = { navController.navigate(MiPeluDestination.SignUp) },
                onForgotPassword = { navController.navigate(MiPeluDestination.ForgotPassword) },
            )
        }

        composable<MiPeluDestination.ForgotPassword> {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable<MiPeluDestination.SignUp> {
            SignUpScreen(
                onBack = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(MiPeluDestination.Home) {
                        popUpTo(MiPeluDestination.Onboarding) { inclusive = true }
                    }
                },
            )
        }

        composable<MiPeluDestination.Home> {
            HomeScreen(
                onNuevaClienta = { navController.navigate(MiPeluDestination.NewClient) },
                onNuevoTrabajo = { navController.navigate(MiPeluDestination.NewWork()) },
                onBuscarHistorial = { navController.navigate(MiPeluDestination.WorkList) },
                onWorkClick = { workId -> navController.navigate(MiPeluDestination.WorkDetail(workId)) },
            )
        }

        composable<MiPeluDestination.ClientList> {
            ClientListScreen(
                onClientClick = { clientId -> navController.navigate(MiPeluDestination.ClientProfile(clientId)) },
                onAddClient = { navController.navigate(MiPeluDestination.NewClient) },
            )
        }

        composable<MiPeluDestination.NewClient> {
            NewClientScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<MiPeluDestination.ClientProfile> {
            ClientProfileScreen(
                onBack = { navController.popBackStack() },
                onNuevoTrabajo = { clientId -> navController.navigate(MiPeluDestination.NewWork(clientId = clientId)) },
                onEditarFicha = { clientId -> navController.navigate(MiPeluDestination.ClientFicha(clientId)) },
                onWorkClick = { workId -> navController.navigate(MiPeluDestination.WorkDetail(workId)) },
            )
        }

        composable<MiPeluDestination.ClientFicha> {
            ClientFichaScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<MiPeluDestination.WorkList> {
            WorkListScreen(
                onWorkClick = { workId -> navController.navigate(MiPeluDestination.WorkDetail(workId)) },
                onAddWork = { navController.navigate(MiPeluDestination.NewWork()) },
            )
        }

        composable<MiPeluDestination.NewWork> {
            NewWorkScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
            )
        }

        composable<MiPeluDestination.WorkDetail> {
            WorkDetailScreen(
                onBack = { navController.popBackStack() },
                onEdit = { workId ->
                    navController.navigate(MiPeluDestination.NewWork(editWorkId = workId))
                },
                onDuplicate = { workId ->
                    navController.navigate(MiPeluDestination.NewWork(duplicateFromWorkId = workId))
                },
            )
        }

        composable<MiPeluDestination.Settings> {
            SettingsScreen(
                onLoggedOut = {
                    navController.navigate(MiPeluDestination.Onboarding) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
