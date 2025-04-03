package com.example.footballstatistics_app_android

sealed class Screen(val route: String) {
    object Home : Screen("Home")
    object Match : Screen("Match/{match_id}"){
        fun createRoute(match_id: Int?) = "Match/$match_id"
    }
    object Calendar : Screen("Calendar")
    data object Profile : Screen("profile")
    object Login: Screen("Login")
    object Register: Screen("Register")
    object AddMatch: Screen("AddMatch")
}