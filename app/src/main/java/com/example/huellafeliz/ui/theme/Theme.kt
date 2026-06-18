package com.example.huellafeliz.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val HuellaFelizColorScheme = lightColorScheme(
    primary          = NaranjaHuellaFeliz,
    onPrimary        = Blanco,
    primaryContainer = NaranjaOscuro,
    secondary        = VerdeDisponible,
    onSecondary      = Blanco,
    background       = Blanco,
    onBackground     = GrisTexto,
    surface          = Blanco,
    onSurface        = GrisTexto,
    error            = RojoError,
    onError          = Blanco,
    outline          = GrisBorde
)

@Composable
fun HuellaFelizTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = NaranjaHuellaFeliz.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = HuellaFelizColorScheme,
        typography  = Typography,
        content     = content
    )
}
