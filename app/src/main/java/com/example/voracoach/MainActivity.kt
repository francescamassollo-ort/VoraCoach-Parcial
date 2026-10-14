package com.example.voracoach

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.voracoach.ui.theme.VoraCoachParcialTheme
import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint permite que Hilt inyecte dependencias en esta Activity
// y en los ViewModels que se pidan desde sus pantallas con hiltViewModel().
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // La Activity solo aplica el tema y monta la navegación; no tiene lógica de pantallas.
            VoraCoachParcialTheme {
                // TODO: AppNavHost()
            }
        }
    }
}
