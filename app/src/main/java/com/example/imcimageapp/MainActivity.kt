package com.example.imcimageapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verifica si el usuario está autenticado al inicio de la actividad
        if (!isUserLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            setContent {
                MainScreen()
            }
        }
    }

    // Función para verificar si el usuario ha iniciado sesión
    private fun isUserLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    @Composable
    fun MainScreen() {
        val context = LocalContext.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Bienvenido a la Calculadora de IMC")

            // Botón para abrir la calculadora de IMC en una nueva pantalla
            Button(
                onClick = {
                    // Iniciar IMCActivity
                    val intent = Intent(context, IMCActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mostrar IMC")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para abrir la actividad de edición de imagen
            Button(
                onClick = {
                    val intent = Intent(context, ImageEditActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Editar Imagen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para cerrar sesión
            Button(
                onClick = {
                    logout()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar Sesión")
            }
        }
    }

    // Método para cerrar sesión
    private fun logout() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", false)
            apply()
        }
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
