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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.presentation.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // 로그인 상태 감지 및 화면 전환
    LaunchedEffect(isLoggedIn) {
        if (!isLoggedIn) {
            navController.navigate(Login.route) {
                popUpTo(0) { inclusive = true } // 백스택 모두 제거
                launchSingleTop = true
            }
        }
    }

    NavHost(navController = navController, startDestination = if (isLoggedIn) Home.route else Login.route) {
        composable(Home.route) {
            HomeScreen(
                onMenuItemClick = { menuItem ->
                    when (menuItem) {
                        ProfileMenuItem.MY_INFO -> navController.navigate(Profile.route)
                        ProfileMenuItem.SETTINGS -> Log.d("NavGraph", "Settings clicked") // TODO: Navigate to Settings Screen
                        ProfileMenuItem.ANNOUNCEMENTS -> navController.navigate(Announcements.route) // Navigate to Announcements Screen
                        ProfileMenuItem.LOGOUT -> {
                            authViewModel.logout()
                            // Logout triggers LaunchedEffect above, navigating to Login
                        }
                    }
                },
                onReportClick = { navController.navigate(Report.route) } // Navigate to ReportScreen
            )
        }
        composable(Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // 로그인 성공 시 Home으로 이동 (중간 단계 생략 가능)
                    navController.navigate(Home.route) {
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
