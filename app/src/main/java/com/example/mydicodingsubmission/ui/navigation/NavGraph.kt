package com.example.mydicodingsubmission.ui.navigation

sealed class NavGraph(val route: String) {
    data object SplashScreen : NavGraph(route = "splash")
    data object LoginScreen : NavGraph(route = "login")
    data object RegisterScreen : NavGraph(route = "register")
    data object MainScreen : NavGraph(route = "home")
    data object DetailScreen : NavGraph(route = "detail")
    data object AboutScreen : NavGraph(route = "profile")
    data object AddFoodScreen : NavGraph(route = "addFood")
}