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
import com.aitor.trackactividades.login.ui.HomeScreen
import com.aitor.trackactividades.login.ui.LoginScreen
import com.aitor.trackactividades.login.ui.LoginViewModel
import com.aitor.trackactividades.login.ui.RegisterScreen
import com.aitor.trackactividades.login.ui.RegisterViewModel

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
                navigateToFeed = { navController.navigate(Feed) },
                loginViewModel = loginViewModel
            )
        }
        composable<Register> {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                navigateToLogin = { navController.navigate(Login) },
                navigateToFeed = { navController.navigate(Feed) },
                registerViewModel = registerViewModel)
        }
        composable<Feed> {
            val feedViewModel: FeedViewModel = hiltViewModel()
            FeedScreen(feedViewModel = feedViewModel)
        }
    }
}