package com.example.imcimageapp

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageEditScreen(
    imageBitmap: Bitmap?,
    overlayText: String,
    textX: Float,
    textY: Float,
    onPickImage: () -> Unit,
    onOverlayTextChange: (String) -> Unit,
    onTextPositionChange: (dx: Float, dy: Float) -> Unit,
    onSaveImage: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editor de Imagen") }
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
                Box(
                    contentAlignment = Alignment.TopStart,
                    modifier = Modifier
                        .size(450.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                onTextPositionChange(dragAmount.x, dragAmount.y)
                            }
                        }
                ) {
                    imageBitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Text(
                        text = overlayText,
                        color = Color.White,
                        fontSize = 40.sp, // Tamaño visible que corresponde a SAVED_TEXT_SIZE
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .offset { IntOffset(textX.roundToInt(), textY.roundToInt()) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = overlayText,
                    onValueChange = onOverlayTextChange,
                    label = { Text("Texto para agregar") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onPickImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cargar Nueva Imagen")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onSaveImage,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Imagen")
                }
            }
        }
    )
}
