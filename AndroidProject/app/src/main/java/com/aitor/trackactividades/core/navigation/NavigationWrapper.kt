package com.aitor.trackactividades.core.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aitor.trackactividades.feed.ui.FeedScreen
import com.aitor.trackactividades.feed.ui.FeedViewModel
import com.aitor.trackactividades.authentication.presentation.HomeScreen
import com.aitor.trackactividades.authentication.presentation.LoginScreen
import com.aitor.trackactividades.authentication.presentation.LoginViewModel
import com.aitor.trackactividades.authentication.presentation.RegisterScreen
import com.aitor.trackactividades.authentication.presentation.RegisterViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(
                navigateToLogin = { navController.navigate(Login) },
                navigateToRegister = { navController.navigate(Register) }
            )
        }
        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                navigateToRegister = { navController.navigate(Register) },
                navigateToFeed = {
                    navController.navigate(Feed) {
                        popUpTo(Login) { inclusive = true }  // Elimina Login de la pila de navegación
                        popUpTo(Register) { inclusive = true }  // Elimina Register de la pila de navegación
                        popUpTo(Home) { inclusive = true }  // Elimina Home de la pila de navegación
                    }
                },
                loginViewModel = loginViewModel
            )
        }
        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                navigateToLogin = { navController.navigate(Login) },
                navigateToFeed = {
                    navController.navigate(Feed) {
                        popUpTo(Login) { inclusive = true }  // Elimina Login de la pila de navegación
                        popUpTo(Register) { inclusive = true }  // Elimina Login de la pila de navegación
                        popUpTo(Home) { inclusive = true }  // Elimina Home de la pila de navegación
                    }
                },
                registerViewModel = registerViewModel)
        }
        composable<Feed> {
            val feedViewModel: FeedViewModel = hiltViewModel()
            FeedScreen(feedViewModel = feedViewModel)
        }
    }
}