package com.example.imcimageapp

import android.content.Context
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

@OptIn(ExperimentalMaterial3Api::class)
class IMCActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IMCScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IMCScreen() {
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    // Obtenemos el nombre de usuario actual de SharedPreferences
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""

    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var lastFiveRecords by remember { mutableStateOf<List<Double>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cálculo de IMC") }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Usuario: $username")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Altura (m)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Peso (kg)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val h = height.toFloatOrNull()
                        val w = weight.toFloatOrNull()
                        if (h != null && w != null) {
                            val imc = w / (h * h)
                            result = "Tu IMC es: ${"%.2f".format(imc)}"
                            databaseHelper.saveImcRecord(username, imc)
                            Toast.makeText(context, "IMC guardado correctamente", Toast.LENGTH_SHORT).show()
                        } else {
                            result = "Ingresa valores válidos"
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calcular IMC")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = result)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        lastFiveRecords = databaseHelper.getLastFiveImcRecords(username)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver últimos 5 cálculos")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Últimos 5 cálculos de IMC:")
                lastFiveRecords.forEach { imc ->
                    Text(text = "IMC: ${"%.2f".format(imc)}")
                }
            }
        }
    )
}
