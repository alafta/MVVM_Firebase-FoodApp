package com.example.mydicodingsubmission.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mydicodingsubmission.ui.screen.AboutScreen
import com.example.mydicodingsubmission.ui.screen.AddFoodScreen
import com.example.mydicodingsubmission.ui.screen.DetailScreen
import com.example.mydicodingsubmission.ui.screen.LoginScreen
import com.example.mydicodingsubmission.ui.screen.MainScreen
import com.example.mydicodingsubmission.ui.screen.RegisterScreen
import com.example.mydicodingsubmission.ui.screen.SplashScreen

@Composable
fun SetupNavGraph (
    navHostController: NavHostController = rememberNavController(),
    authenticationNavigationViewModel: AuthenticationNavigationViewModel = hiltViewModel()
) {
    NavHost (
        navController = navHostController,
        startDestination = NavGraph.SplashScreen.route
    ) {
        composable(route = NavGraph.SplashScreen.route) {
            SplashScreen(
                navHostController = navHostController,
                authenticationNavigationViewModel = authenticationNavigationViewModel
            )
        }
        composable (
            route = NavGraph.MainScreen.route
        ) {
            MainScreen(navHostController = navHostController )
        }
        composable (
            route = NavGraph.DetailScreen.route
        ) {
            DetailScreen(navHostController)
        }
        composable(
            route = NavGraph.LoginScreen.route
        ) {
            LoginScreen(navHostController)
        }
        composable(
            route = NavGraph.RegisterScreen.route
        ) {
            RegisterScreen(navHostController)
        }
        composable(
            route = NavGraph.AboutScreen.route
        ) {
            AboutScreen(navHostController)
        }
        composable(
            route = NavGraph.AddFoodScreen.route
        ) {
            AddFoodScreen(navHostController)
        }
    }
}