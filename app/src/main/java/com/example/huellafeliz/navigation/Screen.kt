package com.example.huellafeliz.navigation

sealed class Screen(val route: String) {
    object Splash    : Screen("splash")
    object Login     : Screen("login")
    object Registro  : Screen("registro")
    object Home      : Screen("home")
    object AcercaDe  : Screen("acerca_de")
    object Publicar  : Screen("publicar?mascotaId={mascotaId}") {
        fun createRoute(id: String? = null) = if (id != null) "publicar?mascotaId=$id" else "publicar"
    }
    object Detalle   : Screen("detalle/{mascotaId}") {
        fun createRoute(id: String) = "detalle/$id"
    }
    object Solicitudes : Screen("solicitudes")
    object DetalleSolicitud : Screen("detalle_solicitud/{solicitudId}") {
        fun createRoute(id: String) = "detalle_solicitud/$id"
    }
    object MisSolicitudes : Screen("mis_solicitudes")
    object Favoritos : Screen("favoritos")
    object PanelRefugio : Screen("panel_refugio")
    object DetalleModeracion : Screen("detalle_moderacion/{mascotaId}") {
        fun createRoute(id: String) = "detalle_moderacion/$id"
    }
    object Perfil : Screen("perfil")
    object Filtros : Screen("filtros")
}
