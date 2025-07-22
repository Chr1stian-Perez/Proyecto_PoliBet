package com.epn.polibet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.epn.polibet.ui.screens.auth.LoginScreen
import com.epn.polibet.ui.screens.auth.RegisterScreen
import com.epn.polibet.ui.screens.dashboard.DashboardScreen
import com.epn.polibet.ui.screens.sports.SportsScreen
import com.epn.polibet.ui.screens.events.EventDetailScreen
import com.epn.polibet.ui.screens.predictions.PredictionsScreen
import com.epn.polibet.ui.screens.profile.ProfileScreen

@Composable
fun PoliBetNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PoliBetDestinations.LOGIN_ROUTE,
        modifier = modifier
    ) {
        // Autenticación
        composable(PoliBetDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(PoliBetDestinations.REGISTER_ROUTE)
                },
                onNavigateToDashboard = {
                    navController.navigate(PoliBetDestinations.DASHBOARD_ROUTE) {
                        popUpTo(PoliBetDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(PoliBetDestinations.REGISTER_ROUTE) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToDashboard = {
                    navController.navigate(PoliBetDestinations.DASHBOARD_ROUTE) {
                        popUpTo(PoliBetDestinations.LOGIN_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard principal
        composable(PoliBetDestinations.DASHBOARD_ROUTE) {
            DashboardScreen(
                onNavigateToSports = { sportId ->
                    navController.navigate("${PoliBetDestinations.SPORTS_ROUTE}/$sportId")
                },
                onNavigateToPredictions = {
                    navController.navigate(PoliBetDestinations.PREDICTIONS_ROUTE)
                },
                onNavigateToProfile = {
                    navController.navigate(PoliBetDestinations.PROFILE_ROUTE)
                }
            )
        }

        // Deportes
        composable(
            route = "${PoliBetDestinations.SPORTS_ROUTE}/{sportId}",
            arguments = listOf(navArgument("sportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sportId = backStackEntry.arguments?.getString("sportId") ?: ""
            SportsScreen(
                sportId = sportId,
                onNavigateToEvent = { eventId ->
                    navController.navigate("${PoliBetDestinations.EVENT_DETAIL_ROUTE}/$eventId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Detalle de evento
        composable(
            route = "${PoliBetDestinations.EVENT_DETAIL_ROUTE}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCreatePrediction = { prediction ->
                    // Navegar a la pantalla de pronósticos después de crear uno
                    navController.navigate(PoliBetDestinations.PREDICTIONS_ROUTE) {
                        // Opcional: limpiar el back stack hasta el dashboard
                        popUpTo(PoliBetDestinations.DASHBOARD_ROUTE)
                    }
                }
            )
        }

        // Pronósticos
        composable(PoliBetDestinations.PREDICTIONS_ROUTE) {
            PredictionsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Perfil
        composable(PoliBetDestinations.PROFILE_ROUTE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogout = {
                    navController.navigate(PoliBetDestinations.LOGIN_ROUTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

object PoliBetDestinations {
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val DASHBOARD_ROUTE = "dashboard"
    const val SPORTS_ROUTE = "sports"
    const val EVENT_DETAIL_ROUTE = "event_detail"
    const val PREDICTIONS_ROUTE = "predictions"
    const val PROFILE_ROUTE = "profile"
}
