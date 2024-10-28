package com.example.imcimageapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen() {
        val context = LocalContext.current
        val databaseHelper = DatabaseHelper(context)

        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        val isValidUser = databaseHelper.validateUser(username, password)
                        if (isValidUser) {
                            saveLoginState(context)
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            Toast.makeText(context, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para abrir la pantalla de registro
            Button(
                onClick = {
                    context.startActivity(Intent(context, RegisterActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }

    private fun saveLoginState(context: Context) {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("is_logged_in", true)
            apply()
        }
    }
}
