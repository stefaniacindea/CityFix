package com.example.licenta

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.licenta.data.User
import com.example.licenta.data.UserRole
import com.example.licenta.ui.admin.AdminMainScreen
import com.example.licenta.ui.auth.AuthScreen
import com.example.licenta.ui.citizen.CategorySelectionScreen
import com.example.licenta.ui.citizen.CitizenMainScreen
import com.example.licenta.viewmodel.AuthViewModel
import com.example.licenta.ui.citizen.ReportDetailScreen
import com.example.licenta.ui.citizen.MyReportsScreen

sealed class Screen(val route: String) {
    object Auth : Screen("auth_screen")
    object CitizenMain : Screen("citizen_main_screen")
    object AdminMain : Screen("admin_main_screen")
    object CategorySelection : Screen("category_selection_screen")
    object ReportDetail : Screen("report_detail_screen")
    object MyReports : Screen("my_reports_screen")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    val navigateBasedOnRole: (User) -> Unit = { user ->
        val destination = when (user.role) {
            UserRole.ADMIN -> Screen.AdminMain.route
            UserRole.CITIZEN -> Screen.CitizenMain.route
            else -> Screen.CitizenMain.route
        }
        navController.navigate(destination) {
            popUpTo(Screen.Auth.route) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                authViewModel = authViewModel,
                onSuccessNavigation = { user ->
                    navigateBasedOnRole(user)
                }
            )
        }

        composable(Screen.CitizenMain.route) {
            CitizenMainScreen(
                onNavigateToSelection = {
                    navController.navigate(Screen.CategorySelection.route)
                },
                onNavigateToMyReports = {
                    navController.navigate(Screen.MyReports.route)
                }
            )
        }

        composable(Screen.CategorySelection.route) {
            CategorySelectionScreen(
                onNavigateToDetails = { reportName ->
                    navController.navigate("${Screen.ReportDetail.route}/$reportName")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Screen.ReportDetail.route}/{reportName}",
            arguments = listOf(navArgument("reportName") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportName = backStackEntry.arguments?.getString("reportName") ?: "Eroare Raport"

            ReportDetailScreen(
                reportName = reportName,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MyReports.route) {
            MyReportsScreen()
        }

        composable(Screen.AdminMain.route) {
            AdminMainScreen()
        }
    }
}