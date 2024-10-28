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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

@Composable
fun RegisterScreen() {
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Nombre de usuario") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = genero, onValueChange = { genero = it }, label = { Text("Género") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = edad, onValueChange = { edad = it }, label = { Text("Edad") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val edadInt = edad.toIntOrNull()
                if (edadInt != null && username.isNotEmpty() && password.isNotEmpty() && nombre.isNotEmpty() && apellido.isNotEmpty() && genero.isNotEmpty()) {
                    val isRegistered = databaseHelper.registerUser(username, password, nombre, apellido, genero, edadInt)
                    if (isRegistered) {
                        Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    } else {
                        Toast.makeText(context, "Error en el registro. Intente con otro usuario.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Por favor, ingrese todos los datos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar")
        }
    }
}
