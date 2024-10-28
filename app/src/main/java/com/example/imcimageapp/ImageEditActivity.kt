package com.example.imcimageapp

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ImageEditActivity : ComponentActivity() {
    private val REQUEST_IMAGE_PICK = 1
    private val TEXT_SIZE = 120f // Tamaño del texto unificado para vista y guardado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
            var overlayText by remember { mutableStateOf("") }
            var textX by remember { mutableStateOf(0f) }
            var textY by remember { mutableStateOf(0f) }

            ImageEditScreen(
                imageBitmap = imageBitmap,
                overlayText = overlayText,
                textX = textX,
                textY = textY,
                onPickImage = { pickImage() },
                onOverlayTextChange = { overlayText = it },
                onTextPositionChange = { dx, dy ->
                    textX += dx
                    textY += dy
                },
                onSaveImage = {
                    if (imageBitmap != null && overlayText.isNotEmpty()) {
                        val updatedBitmap = addTextToBitmap(imageBitmap!!, overlayText, textX, textY, TEXT_SIZE)
                        saveImage(updatedBitmap)
                    } else {
                        Toast.makeText(this, "No hay imagen o texto para guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data ?: return
            val bitmap = loadImageBitmap(imageUri)
            if (bitmap != null) {
                setContent {
                    var imageBitmap by remember { mutableStateOf(bitmap) }
                    var overlayText by remember { mutableStateOf("") }
                    var textX by remember { mutableStateOf(0f) }
                    var textY by remember { mutableStateOf(0f) }
                    ImageEditScreen(
                        imageBitmap = imageBitmap,
                        overlayText = overlayText,
                        textX = textX,
                        textY = textY,
                        onPickImage = { pickImage() },
                        onOverlayTextChange = { overlayText = it },
                        onTextPositionChange = { dx, dy ->
                            textX += dx
                            textY += dy
                        },
                        onSaveImage = {
                            if (imageBitmap != null && overlayText.isNotEmpty()) {
                                val updatedBitmap = addTextToBitmap(imageBitmap!!, overlayText, textX, textY, TEXT_SIZE)
                                saveImage(updatedBitmap)
                            } else {
                                Toast.makeText(this@ImageEditActivity, "No hay imagen o texto para guardar", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    private fun loadImageBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(contentResolver, uri)
                )
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun addTextToBitmap(
        originalBitmap: Bitmap,
        text: String,
        x: Float,
        y: Float,
        textSize: Float
    ): Bitmap {
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint()

        paint.color = Color.WHITE
        paint.textSize = textSize
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.LEFT
        paint.setShadowLayer(2f, 0f, 2f, Color.BLACK)

        canvas.drawText(text, x, y, paint)
        return mutableBitmap
    }

    private fun saveImage(bitmap: Bitmap) {
        val filename = "Edited_Image_${System.currentTimeMillis()}.jpg"
        val fos: OutputStream?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentResolver = applicationContext.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/EditedImages")
            }
            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = imageUri?.let { contentResolver.openOutputStream(it) }
        } else {
            val imagesDir = applicationContext.getExternalFilesDir(null)?.absolutePath
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
        } ?: Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
    }
}
