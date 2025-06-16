package com.aitor.trackactividades.core.navigation

import android.util.Log
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
import com.aitor.trackactividades.buscarUsuario.presentation.SearchScreen
import com.aitor.trackactividades.buscarUsuario.presentation.SearchViewModel
import com.aitor.trackactividades.feed.presentation.ActivityScreen
import com.aitor.trackactividades.feed.presentation.ActivityViewModel
import com.aitor.trackactividades.quedadas.presentation.FormularioQuedadaScreen
import com.aitor.trackactividades.quedadas.presentation.FormularioQuedadaViewModel
import com.aitor.trackactividades.historialActividades.presentation.HistorialScreen
import com.aitor.trackactividades.historialActividades.presentation.HistorialViewModel
import com.aitor.trackactividades.perfil.presentation.PerfilScreen
import com.aitor.trackactividades.perfil.presentation.PerfilViewModel
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel
import com.aitor.trackactividades.quedadas.presentation.DetallesQuedadaScreen
import com.aitor.trackactividades.quedadas.presentation.DetallesQuedadaViewModel
import com.aitor.trackactividades.quedadas.presentation.QuedadasScreen
import com.aitor.trackactividades.quedadas.presentation.QuedadasViewModel
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
            val postInteractionViewModel: PostInteractionViewModel = hiltViewModel()
            FeedScreen(
                navigateToStartRecordActivity = { navController.navigate(RecordActivity) },
                navigateToActivity = { publicationId ->
                    navController.navigate("activity/$publicationId")
                },
                feedViewModel = feedViewModel,
                navigateToProfile = { profileId ->
                    navController.navigate("profile/$profileId")
                },
                navigateToSearch = { navController.navigate(Search) },
                postInteractionViewModel = postInteractionViewModel,
                navigateToHistorial = { navController.navigate(Historial) },
                navigateToQuedadas = { navController.navigate(Quedadas) }
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
        composable(
            route = Profile.ROUTE,
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            val perfilViewModel: PerfilViewModel = hiltViewModel()
            val postInteractionViewModel: PostInteractionViewModel = hiltViewModel()

            // Pasa el ID al ViewModel
            LaunchedEffect(userId) {
                userId?.let { perfilViewModel.loadPerfil(it) }
            }

            PerfilScreen(
                navigateToHome = {
                    navController.navigate(Home) {
                        popUpTo(0)
                    }
                },
                navigateToActivity = { publicationId ->
                    navController.navigate("activity/$publicationId")
                },
                perfilViewModel = perfilViewModel,
                postInteractionViewModel = postInteractionViewModel,
                navigateToHistorial = { navController.navigate(Historial) },
                navigateToQuedadas = { navController.navigate(Quedadas) },
                navigateToFeed = { navController.navigate(Feed) },
                navigateToStartRecordActivity = { navController.navigate(RecordActivity) }
            )
        }
        composable(
            route = DetallesQuedada.ROUTE,
            arguments = listOf(
                navArgument("quedadaId") { type = NavType.LongType }
            )
        ){ backStackEntry ->
            val quedadaId = backStackEntry.arguments?.getLong("quedadaId")
            val detallesQuedadaViewModel: DetallesQuedadaViewModel = hiltViewModel()

            // Pasa el ID al ViewModel
            LaunchedEffect(quedadaId) {
                quedadaId?.let { detallesQuedadaViewModel.loadQuedada(it) }
            }

            DetallesQuedadaScreen(
                detallesQuedadaViewModel = detallesQuedadaViewModel,
                navigateToQuedadas = { navController.navigate(Quedadas) },
                navigateToProfile = { profileId ->
                    navController.navigate("profile/$profileId")
                }
            )
        }
        composable<Search> {
            val searchViewModel: SearchViewModel = hiltViewModel()
            SearchScreen(
                searchViewModel = searchViewModel,
                navigateToProfile = { profileId ->
                    navController.navigate("profile/$profileId")
                },
                navigateToFeed = { navController.navigate(Feed) }
            )
        }
        composable<Historial> {
            val historialViewModel: HistorialViewModel = hiltViewModel()
            HistorialScreen(
                historialViewModel = historialViewModel,
                navigateToActivity = { publicationId ->
                    navController.navigate("activity/$publicationId")
                },
                navigateToStartRecordActivity = { navController.navigate(RecordActivity) },
                navigateToFeed = { navController.navigate(Feed) },
                navigateToQuedadas = { navController.navigate(Quedadas) },
                navigateToProfile = { profileId ->
                    navController.navigate("profile/$profileId")
                }
            )
        }
        composable<Quedadas> {
            val quedadasViewModel: QuedadasViewModel = hiltViewModel()
            QuedadasScreen(
                quedadasViewModel = quedadasViewModel,
                navigateToStartRecordActivity = { navController.navigate(RecordActivity) },
                navigateToFeed = { navController.navigate(Feed) },
                navigateToHistorial = { navController.navigate(Historial) },
                navigateToFormularioQuedada = { navController.navigate(FormularioQuedada) },
                navigateToDetallesQuedada = { quedadaId ->
                    navController.navigate(DetallesQuedada.createRoute(quedadaId))
                },
                navigateToProfile = { profileId ->
                    navController.navigate("profile/$profileId")
                }
            )
        }
        composable<FormularioQuedada> {
            val formularioQuedadaViewModel: FormularioQuedadaViewModel = hiltViewModel()
            FormularioQuedadaScreen(
                formularioQuedadaViewModel = formularioQuedadaViewModel,
                navigateToQuedadas = { navController.navigate(Quedadas) }
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
