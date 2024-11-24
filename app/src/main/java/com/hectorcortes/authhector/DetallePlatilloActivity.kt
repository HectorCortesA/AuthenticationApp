package com.hectorcortes.authhector

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayInputStream

class DetallePlatilloActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_platillo)

        // Recibir los datos del platillo
        val nombrePlatillo = intent.getStringExtra("nombre") ?: "Nombre no disponible"
        val descripcionPlatillo = intent.getStringExtra("descripcion") ?: "Descripción no disponible"
        val ingredientesPlatillo = intent.getStringExtra("ingredientes") ?: "Ingredientes no disponibles"
        val pasosPlatillo = intent.getStringExtra("pasos") ?: "Pasos no disponibles"
        val imagenBase64 = intent.getStringExtra("imagenBase64") ?: ""

        // Inicializar los elementos de la vista
        val nombreTextView: TextView = findViewById(R.id.textViewNombrePlatilloDetalle)
        val descripcionTextView: TextView = findViewById(R.id.textViewDescripcionPlatilloDetalle)
        val ingredientesTextView: TextView = findViewById(R.id.textViewIngredientesPlatilloDetalle)
        val pasosTextView: TextView = findViewById(R.id.textViewPasosPlatilloDetalle)
        val imagenImageView: ImageView = findViewById(R.id.imageViewPlatilloDetalle)

        // Asignar los valores recibidos a las vistas
        nombreTextView.text = nombrePlatillo
        descripcionTextView.text = descripcionPlatillo
        ingredientesTextView.text = ingredientesPlatillo
        pasosTextView.text = pasosPlatillo

        // Convertir la imagen Base64 a Bitmap y asignarla al ImageView
        if (imagenBase64.isNotEmpty()) {
            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
            imagenImageView.setImageBitmap(bitmap)
        }
    }
}
