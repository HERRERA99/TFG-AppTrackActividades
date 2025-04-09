package com.aitor.trackactividades.core.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aitor.trackactividades.feed.presentation.FeedScreen
import com.aitor.trackactividades.feed.presentation.FeedViewModel
import com.aitor.trackactividades.authentication.presentation.HomeScreen
import com.aitor.trackactividades.authentication.presentation.LoginScreen
import com.aitor.trackactividades.authentication.presentation.LoginViewModel
import com.aitor.trackactividades.authentication.presentation.RegisterScreen
import com.aitor.trackactividades.authentication.presentation.RegisterViewModel
import com.aitor.trackactividades.authentication.presentation.SplashScreen
import com.aitor.trackactividades.authentication.presentation.SplashViewModel
import com.aitor.trackactividades.feed.presentation.ActivityScreen
import com.aitor.trackactividades.feed.presentation.ActivityViewModel
import com.aitor.trackactividades.recordActivity.presentation.RecordActivityScreen
import com.aitor.trackactividades.recordActivity.presentation.RecordActivityViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    TrackNavigationStack(navController)

    NavHost(navController = navController, startDestination = Splash) {
        composable<Splash> {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                    }
                },
                navigateToFeed = {
                    navController.navigate(Feed) {
                        popUpTo(0)
                    }
                },
                splashViewModel = splashViewModel
            )
        }
        composable<Home> {
            HomeScreen(
                navigateToLogin = { navController.navigate(Login) },
                navigateToRegister = { navController.navigate(Register) },
                navigateToFeed = {
                    navController.navigate(Feed) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<Login> {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                navigateToRegister = { navController.navigate(Register) },
                navigateToFeed = {
                    navController.navigate(Feed) {
                        popUpTo(0)
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
                        popUpTo(0)
                    }
                },
                registerViewModel = registerViewModel
            )
        }
        composable<Feed> {
            val feedViewModel: FeedViewModel = hiltViewModel()
            FeedScreen(
                navigateToStartRecordActivity = { navController.navigate(RecordActivity) },
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                    }
                },
                navigateToActivity = { publicationId ->
                    navController.navigate("activity/$publicationId")
                },
                feedViewModel = feedViewModel
            )
        }
        composable<RecordActivity> {
            val recordActivityViewModel: RecordActivityViewModel = hiltViewModel()
            RecordActivityScreen(
                recordActivityViewModel = recordActivityViewModel,
                navigateToFeed = { navController.navigate(Feed) }
            )
        }
        composable(
            route = Activity.ROUTE,
            arguments = listOf(
                navArgument("publicationId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val publicationId = backStackEntry.arguments?.getLong("publicationId")
            val activityViewModel: ActivityViewModel = hiltViewModel()

            // Pasa el ID al ViewModel
            LaunchedEffect(publicationId) {
                publicationId?.let { activityViewModel.loadPublication(it) }
            }

            ActivityScreen(
                navigateToHome = { navController.navigate(Home) },
                activityViewModel = activityViewModel
            )
        }
    }
}

@Composable
fun TrackNavigationStack(navController: NavController) {
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collectLatest { _ ->
            val stackRoutes =
                navController.previousBackStackEntry?.destination?.route?.let { listOf(it) }
                    ?: emptyList()
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            val fullStack = (stackRoutes + listOfNotNull(currentRoute)).joinToString(" -> ")

            Log.d("NavBackStack", "Pila actual: $fullStack")
        }
    }
}
