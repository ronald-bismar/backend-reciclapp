package com.example.reciclapp.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.reciclapp.domain.usecases.user_preferences.GetUserPreferencesUseCase
import com.example.reciclapp.presentation.ui.ayuda.ui.SimpleAyudaScreen
import com.example.reciclapp.presentation.ui.login.ui.LoginScreen
import com.example.reciclapp.presentation.ui.login.ui.LoginViewModel
import com.example.reciclapp.presentation.ui.menu.ui.IntroductionScreen
import com.example.reciclapp.presentation.ui.menu.ui.PantallaPresentacion
import com.example.reciclapp.presentation.ui.menu.ui.PantallaPrincipal
import com.example.reciclapp.presentation.ui.menu.ui.PresentacionAppScreen
import com.example.reciclapp.presentation.ui.menu.ui.SocialMediaScreenVendedores
import com.example.reciclapp.presentation.ui.menu.ui.UserTypeScreen
import com.example.reciclapp.presentation.ui.menu.ui.vistas.Comprador
import com.example.reciclapp.presentation.ui.menu.ui.vistas.Vendedor
import com.example.reciclapp.presentation.ui.menu.ui.vistas.mapa.MapsView
import com.example.reciclapp.presentation.ui.registro.ui.RegistroScreen
import com.example.reciclapp.presentation.ui.registro.ui.RegistroViewModel
import com.example.reciclapp.presentation.ui.splash.SplashScreenContent
import com.example.reciclapp.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    mainNavController: NavHostController,
    loginViewModel: LoginViewModel = hiltViewModel(),
    registroViewModel: RegistroViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    getUserPreferencesUseCase: GetUserPreferencesUseCase
) {

    var nextScreen by remember {
        mutableStateOf("splash")
    }

    LaunchedEffect(Unit) {
        nextScreen =
        getNextScreen(getUserPreferencesUseCase)
        /* "tipoDeUsuario"*/
        /*"pantalla presentacion"*/
    }

    NavHost(navController = mainNavController, startDestination = "splash") {
        composable("splash") {
            SplashScreenContent {
                mainNavController.navigate(nextScreen) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("socialmediascreen") {
            SocialMediaScreenVendedores(mainNavController)
        }
        composable("login") {
            LoginScreen(loginViewModel, mainNavController)
        }
        composable("registro") {
            RegistroScreen(registroViewModel, mainNavController)
        }
        composable("menu") {
            PantallaPrincipal(mainNavController)
        }
        composable("pantalla presentacion") {
            PantallaPresentacion(mainNavController)
        }
        composable("presentacion app") {
            PresentacionAppScreen(mainNavController)
        }
        composable("introduction screen") {
            IntroductionScreen(mainNavController)
        }
        composable("Que es Reciclapp"){
            SimpleAyudaScreen()
        }
        composable("tipoDeUsuario") {
            UserTypeScreen(mainNavController)
        }

        composable(
            route = "compradorPerfil/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Comprador(mainNavController = mainNavController, compradorId = userId)
        }
        composable(
            route = "vendedorPerfil/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Vendedor(navController = mainNavController, vendedorId = userId)
        }
        composable("map") {
            userViewModel.user.observeAsState().value?.idUsuario?.let { idUsuario ->
                MapsView(idUsuario = idUsuario, mainNavController = mainNavController)
            }
        }
    }
}

suspend fun getNextScreen(getUserPreferencesUseCase: GetUserPreferencesUseCase): String {
    return withContext(Dispatchers.IO) {
        if (getUserPreferencesUseCase.execute()?.nombre != "") "menu" else "login"
    }
}
