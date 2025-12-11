package mx.edu.utng.alertavecinal.ui.navigation

/*
Clase Navigation: Esta clase define toda la estructura de navegación
de la aplicación utilizando Jetpack Navigation Compose. Gestiona las
rutas entre todas las pantallas, controla el acceso basado en autenticación
y roles de usuario (usuario regular vs moderador/administrador), y proporciona
funciones de extensión para navegar fácilmente entre pantallas desde cualquier
parte de la app.


*/

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.maps.model.LatLng
import mx.edu.utng.alertavecinal.BuildConfig
import mx.edu.utng.alertavecinal.utils.Constants
import mx.edu.utng.alertavecinal.ui.screens.*
import mx.edu.utng.alertavecinal.viewmodel.AuthViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Constants.ROUTE_WELCOME
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.checkCurrentUser()
    }

    // Determinar la pantalla de inicio basada en el rol del usuario
    val initialDestination = when (authState.currentUser?.role?.name) {
        Constants.ROLE_MODERATOR -> Constants.ROUTE_MODERATOR_DASHBOARD
        Constants.ROLE_ADMIN -> Constants.ROUTE_MODERATOR_DASHBOARD
        else -> startDestination
    }

    NavHost(
        navController = navController,
        startDestination = initialDestination
    ) {
        composable(Constants.ROUTE_WELCOME) {
            WelcomeScreen(navController = navController)
        }

        composable(Constants.ROUTE_LOGIN) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }

        composable(Constants.ROUTE_REGISTER) {
            RegisterScreen(navController = navController)
        }

        composable(Constants.ROUTE_MAP) {
            if (authState.isAuthenticated) {
                MapScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN) {
                        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                    }
                }
            }
        }

        composable(Constants.ROUTE_CREATE_REPORT) {
            if (authState.isAuthenticated) {
                CreateReportScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_SELECT_LOCATION) {
            if (authState.isAuthenticated) {
                SelectLocationScreen(
                    navController = navController,
                    onLocationSelected = { latLng ->
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_location",
                            latLng
                        )
                        navController.popBackStack()
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_PENDING_REPORTS) {
            if (authState.isAuthenticated) {
                val currentUser = authState.currentUser
                if (currentUser?.role?.name == Constants.ROLE_MODERATOR ||
                    currentUser?.role?.name == Constants.ROLE_ADMIN) {
                    PendingReportsScreen(navController = navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_PROFILE) {
            if (authState.isAuthenticated) {
                ProfileScreen(navController = navController)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable("${Constants.ROUTE_REPORT_DETAIL}/{${Constants.KEY_REPORT_ID}}") { backStackEntry ->
            if (authState.isAuthenticated) {
                val reportId = backStackEntry.arguments?.getString(Constants.KEY_REPORT_ID) ?: ""
                ReportDetailScreen(navController = navController, reportId = reportId)
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate(Constants.ROUTE_LOGIN)
                }
            }
        }

        composable(Constants.ROUTE_MODERATOR_DASHBOARD) {
            if (BuildConfig.DEBUG) {
                ModeratorDashboardScreen(
                    navController = navController,
                    authViewModel = authViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            } else {
                // En producción, verificar autenticación
                if (authState.isAuthenticated) {
                    val currentUser = authState.currentUser
                    if (currentUser?.role?.name == Constants.ROLE_MODERATOR ||
                        currentUser?.role?.name == Constants.ROLE_ADMIN) {
                        ModeratorDashboardScreen(
                            navController = navController,
                            authViewModel = authViewModel,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    } else {
                        // Si no es moderador, redirigir al mapa
                        LaunchedEffect(Unit) {
                            navController.navigate(Constants.ROUTE_MAP) {
                                popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                            }
                        }
                    }
                } else {
                    // Si no está autenticado, redirigir al login
                    LaunchedEffect(Unit) {
                        navController.navigate(Constants.ROUTE_LOGIN) {
                            popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
                        }
                    }
                }
            }
        }


        composable(
            "${Constants.ROUTE_MODERATOR_REVIEW}/{${Constants.KEY_REPORT_ID}}/{${Constants.KEY_MODERATOR_ID}}/{${Constants.KEY_MODERATOR_NAME}}",
            arguments = listOf(
                navArgument(Constants.KEY_REPORT_ID) { type = NavType.StringType },
                navArgument(Constants.KEY_MODERATOR_ID) { type = NavType.StringType },
                navArgument(Constants.KEY_MODERATOR_NAME) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString(Constants.KEY_REPORT_ID) ?: ""
            val moderatorId = backStackEntry.arguments?.getString(Constants.KEY_MODERATOR_ID) ?: ""
            val moderatorName = backStackEntry.arguments?.getString(Constants.KEY_MODERATOR_NAME) ?: ""

            ModeratorReportReviewScreen(
                reportId = reportId,
                moderatorId = moderatorId,
                moderatorName = moderatorName,
                onBack = { navController.popBackStack() },
                onReportUpdated = {
                    navController.popBackStack(Constants.ROUTE_MODERATOR_DASHBOARD, false)
                }
            )
        }
    }
}

// Función para navegar a selección de ubicación
fun NavHostController.navigateToSelectLocation() {
    navigate(Constants.ROUTE_SELECT_LOCATION) {
        launchSingleTop = true
    }
}


fun NavHostController.navigateToMap() {
    navigate(Constants.ROUTE_MAP) {
        popUpTo(Constants.ROUTE_WELCOME) { inclusive = true }
    }
}

fun NavHostController.navigateToLogin() {
    navigate(Constants.ROUTE_LOGIN) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToRegister() {
    navigate(Constants.ROUTE_REGISTER) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToCreateReport() {
    navigate(Constants.ROUTE_CREATE_REPORT) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToProfile() {
    navigate(Constants.ROUTE_PROFILE) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToPendingReports() {
    navigate(Constants.ROUTE_PENDING_REPORTS) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToReportDetail(reportId: String) {
    navigate("${Constants.ROUTE_REPORT_DETAIL}/$reportId") {
        launchSingleTop = true
    }
}

fun NavHostController.navigateToModeratorDashboard() {
    navigate(Constants.ROUTE_MODERATOR_DASHBOARD) {
        // Limpiar stack si venimos de login
        popUpTo(Constants.ROUTE_LOGIN) { inclusive = true }
        launchSingleTop = true
    }
}

fun NavHostController.navigateToModeratorReview(
    reportId: String,
    moderatorId: String,
    moderatorName: String
) {
    navigate("${Constants.ROUTE_MODERATOR_REVIEW}/$reportId/$moderatorId/$moderatorName") {
        launchSingleTop = true
    }
}

fun NavHostController.navigateBack() {
    popBackStack()
}

fun NavHostController.navigateToWelcomeAndClearStack() {
    navigate(Constants.ROUTE_WELCOME) {
        popUpTo(0) { inclusive = true }
    }
}

fun getStartDestinationByRole(userRole: String?): String {
    return when (userRole) {
        Constants.  ROLE_MODERATOR, Constants.ROLE_ADMIN -> Constants.ROUTE_MODERATOR_DASHBOARD
        else -> Constants.ROUTE_MAP
    }
}