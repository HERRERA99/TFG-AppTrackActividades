package com.aitor.trackactividades.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aitor.trackactividades.login.ui.RegisterScreen
import com.aitor.trackactividades.login.ui.StartScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            StartScreen {
                navController.navigate(Register)
            }
        }
        composable<Register> {
            RegisterScreen()
        }

    }
}