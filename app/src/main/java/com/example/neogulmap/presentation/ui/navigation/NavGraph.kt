package com.example.neogulmap.presentation.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.neogulmap.presentation.ui.components.ProfileMenuItem
import com.example.neogulmap.presentation.ui.screens.HomeScreen
import com.example.neogulmap.presentation.ui.screens.LoginScreen
import com.example.neogulmap.presentation.ui.screens.TermsScreen
import com.example.neogulmap.presentation.ui.screens.SignupScreen
import com.example.neogulmap.presentation.ui.screens.ProfileScreen
import com.example.neogulmap.presentation.ui.screens.AnnouncementsScreen
import com.example.neogulmap.presentation.ui.screens.ReportScreen // Import ReportScreen
import com.example.neogulmap.presentation.ui.navigation.Screen.* // Import all screen objects

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Login.route) {
        composable(Home.route) {
            HomeScreen(
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        ProfileMenuItem.MY_INFO -> navController.navigate(Profile.route)
                        ProfileMenuItem.SETTINGS -> Log.d("NavGraph", "Settings clicked") // TODO: Navigate to Settings Screen
                        ProfileMenuItem.ANNOUNCEMENTS -> navController.navigate(Announcements.route) // Navigate to Announcements Screen
                        ProfileMenuItem.LOGOUT -> Log.d("NavGraph", "Logout clicked") // TODO: Implement Logout logic
                    }
                },
                onReportClick = { navController.navigate(Report.route) } // Navigate to ReportScreen
            )
        }
        composable(Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Terms.route) {
                        popUpTo(Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Terms.route) {
            TermsScreen(
                onTermsAgreed = {
                    navController.navigate(Signup.route) {
                        popUpTo(Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Signup.route) {
            SignupScreen(
                onSignupComplete = {
                    navController.navigate(Home.route) {
                        popUpTo(Login.route) { inclusive = true } // Clear login/terms/signup from backstack
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Profile.route) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Announcements.route) {
            AnnouncementsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Report.route) { // Add new composable for ReportScreen
            ReportScreen(onReportSuccess = { navController.popBackStack() })
        }
    }
}
