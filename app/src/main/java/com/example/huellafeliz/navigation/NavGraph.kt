package com.example.huellafeliz.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.huellafeliz.ui.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    val mascotaViewModel: MascotaViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController  = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, viewModel = authViewModel)
        }
        composable(Screen.Registro.route) {
            RegistroScreen(navController, viewModel = authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, viewModel = authViewModel, mascotaViewModel = mascotaViewModel)
        }
        composable(Screen.AcercaDe.route) {
            AcercaDeScreen(navController)
        }
        composable(
            route = Screen.Publicar.route,
            arguments = listOf(
                androidx.navigation.navArgument("mascotaId") { 
                    type = androidx.navigation.NavType.StringType
                    nullable = true
                    defaultValue = null 
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("mascotaId")
            PublicarMascotaScreen(navController, id, viewModel = mascotaViewModel, authViewModel = authViewModel)
        }
        composable(Screen.Detalle.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("mascotaId") ?: ""
            DetalleMascotaScreen(navController, id, authViewModel = authViewModel)
        }
        composable(Screen.Solicitudes.route) {
            SolicitudesScreen(navController, authViewModel = authViewModel)
        }
        composable(Screen.DetalleSolicitud.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""
            DetalleSolicitudScreen(navController, id)
        }
        composable(Screen.MisSolicitudes.route) {
            MisSolicitudesScreen(navController, authViewModel = authViewModel)
        }
        composable(Screen.PanelRefugio.route) {
            PanelRefugioScreen(navController, authViewModel = authViewModel, mascotaViewModel = mascotaViewModel)
        }
        composable(Screen.DetalleModeracion.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("mascotaId") ?: ""
            DetalleModeracionScreen(navController, id)
        }
        composable(Screen.Favoritos.route) {
            FavoritosScreen(navController, authViewModel = authViewModel)
        }
        composable(Screen.Perfil.route) {
            PerfilScreen(navController, authViewModel = authViewModel)
        }
        composable(Screen.Filtros.route) {
            FiltrosScreen(navController, viewModel = mascotaViewModel)
        }
    }
}
