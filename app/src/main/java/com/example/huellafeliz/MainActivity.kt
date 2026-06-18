package com.example.huellafeliz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.huellafeliz.data.network.CloudinaryService
import com.example.huellafeliz.navigation.NavGraph
import com.example.huellafeliz.ui.theme.HuellaFelizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Inicializar Cloudinary
        CloudinaryService.init(this)

        setContent {
            HuellaFelizTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
