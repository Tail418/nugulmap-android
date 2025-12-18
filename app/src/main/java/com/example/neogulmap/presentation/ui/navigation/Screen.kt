package com.example.neogulmap.presentation.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Login : Screen("login")
    object ZoneDetail : Screen("zone_detail/{zoneId}") {
        fun createRoute(zoneId: Long) = "zone_detail/$zoneId"
    }
    object Report : Screen("report")
    object Profile : Screen("profile")
    object Announcements : Screen("announcements")
    object Terms : Screen("terms")
    object Signup : Screen("signup")
}
