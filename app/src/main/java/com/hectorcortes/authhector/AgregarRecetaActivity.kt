package com.hectorcortes.authhector

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream

class AgregarRecetaActivity : AppCompatActivity() {

    private lateinit var editTextNombre: EditText
    private lateinit var editTextDescripcion: EditText
    private lateinit var editTextIngredientes: EditText
    private lateinit var editTextPasos: EditText
    private lateinit var spinnerCategoria: Spinner // Spinner para la categoría
    private lateinit var buttonSeleccionarImagen: Button
    private lateinit var imageViewSeleccionada: ImageView
    private lateinit var buttonGuardarReceta: Button

    private var imagenUri: Uri? = null // Para almacenar la URI de la imagen seleccionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_receta)

        // Inicializar vistas
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextDescripcion = findViewById(R.id.editTextDescripcion)
        editTextIngredientes = findViewById(R.id.editTextIngredientes)
        editTextPasos = findViewById(R.id.editTextPasos)
        spinnerCategoria = findViewById(R.id.spinnerCategoria) // Spinner de categoría
        buttonSeleccionarImagen = findViewById(R.id.buttonSeleccionarImagen)
        imageViewSeleccionada = findViewById(R.id.imageViewSeleccionada)
        buttonGuardarReceta = findViewById(R.id.buttonGuardarReceta)

        // Configurar Spinner de categorías
        configurarSpinnerCategorias()

        // Acción para seleccionar imagen
        buttonSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }

        // Acción para guardar receta
        buttonGuardarReceta.setOnClickListener {
            guardarReceta()
        }
    }

    private fun configurarSpinnerCategorias() {
        val categorias = listOf("Selecciona una categoría", "Postres", "Poblanos", "Ensaladas", "Sopas")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            imagenUri = data?.data
            imageViewSeleccionada.setImageURI(imagenUri)
        }
    }

    private fun guardarReceta() {
        val nombre = editTextNombre.text.toString()
        val descripcion = editTextDescripcion.text.toString()
        val ingredientes = editTextIngredientes.text.toString()
        val pasos = editTextPasos.text.toString()
        val categoria = spinnerCategoria.selectedItem.toString()

        if (nombre.isNotEmpty() && descripcion.isNotEmpty() && ingredientes.isNotEmpty() && pasos.isNotEmpty() && imagenUri != null && categoria != "Selecciona una categoría") {
            // Llamar a la función para guardar la receta en Firebase
            guardarRecetaEnFirebase(nombre, descripcion, ingredientes, pasos, categoria, imagenUri!!)
        } else {
            Toast.makeText(this, "Por favor, llena todos los campos, selecciona una categoría e imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarRecetaEnFirebase(
        nombre: String,
        descripcion: String,
        ingredientes: String,
        pasos: String,
        categoria: String,
        imagenUri: Uri
    ) {
        try {
            // Leer la imagen seleccionada como Bitmap
            val inputStream = contentResolver.openInputStream(imagenUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Convertir el Bitmap a Base64
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream) // Reducir calidad al 50%
            val imageBytes = byteArrayOutputStream.toByteArray()
            val imageBase64 = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

            // Crear el mapa con los datos de la receta
            val receta = hashMapOf(
                "nombre" to nombre,
                "descripcion" to descripcion,
                "ingredientes" to ingredientes,
                "pasos" to pasos,
                "categoria" to categoria, // Guardar la categoría
                "imagenBase64" to imageBase64
            )

            // Guardar los datos en Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("recetas")
                .add(receta)
                .addOnSuccessListener {
                    Toast.makeText(this, "Receta guardada con éxito", Toast.LENGTH_SHORT).show()
                    finish() // Cierra la actividad después de guardar
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar la receta: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar la imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
