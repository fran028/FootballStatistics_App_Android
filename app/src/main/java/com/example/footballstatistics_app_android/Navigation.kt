package com.example.footballstatistics_app_android

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object Match : Screen("Match")
    object Setting : Screen("Setting")
    object Calendar : Screen("Calendar")
    data object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }
    object Login: Screen("Login")
    object Register: Screen("Register")

}