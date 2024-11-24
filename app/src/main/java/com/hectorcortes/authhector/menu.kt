package com.hectorcortes.authhector

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.hectorcortes.authhector.models.Platillo

class menu : AppCompatActivity() {

    // RecyclerViews y listas para categorías
    private lateinit var recyclerTopPlatillos: RecyclerView
    private lateinit var recyclerPoblanos: RecyclerView
    private lateinit var recyclerPostres: RecyclerView

    private val topPlatillosList = mutableListOf<QueryDocumentSnapshot>()
    private val poblanosList = mutableListOf<QueryDocumentSnapshot>()
    private val postresList = mutableListOf<QueryDocumentSnapshot>()

    private lateinit var adapterTopPlatillos: PlatilloAdapter
    private lateinit var adapterPoblanos: PlatilloAdapter
    private lateinit var adapterPostres: PlatilloAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_menu)

        // Inicializar botones
        val perfil123 = findViewById<Button>(R.id.perfil123)
        val subir1 = findViewById<Button>(R.id.subir1)

        // Configurar RecyclerViews
        recyclerTopPlatillos = findViewById(R.id.recyclerTopPlatillos)
        recyclerTopPlatillos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerPoblanos = findViewById(R.id.recyclerPoblanos)
        recyclerPoblanos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerPostres = findViewById(R.id.recyclerPostres)
        recyclerPostres.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Inicializar adaptadores
        adapterTopPlatillos = PlatilloAdapter(topPlatillosList) { platillo ->
            Toast.makeText(this, "Ver receta de ${platillo.getString("nombre")}", Toast.LENGTH_SHORT).show()
        }
        adapterPoblanos = PlatilloAdapter(poblanosList) { platillo ->
            Toast.makeText(this, "Ver receta de ${platillo.getString("nombre")}", Toast.LENGTH_SHORT).show()
        }
        adapterPostres = PlatilloAdapter(postresList) { platillo ->
            Toast.makeText(this, "Ver receta de ${platillo.getString("nombre")}", Toast.LENGTH_SHORT).show()
        }

        recyclerTopPlatillos.adapter = adapterTopPlatillos
        recyclerPoblanos.adapter = adapterPoblanos
        recyclerPostres.adapter = adapterPostres

        // Cargar datos de Firebase
        cargarPlatillos()

        // Acción para el botón 'perfil123'
        perfil123.setOnClickListener {
            val perfilIntent = Intent(this, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(perfilIntent)
        }

        // Acción para el botón 'subir1'
        subir1.setOnClickListener {
            val agregarRecetaIntent = Intent(this, AgregarRecetaActivity::class.java)
            startActivity(agregarRecetaIntent)
        }
    }

    private fun cargarPlatillos() {
        val db = FirebaseFirestore.getInstance()

        db.collection("recetas")
            .get()
            .addOnSuccessListener { result ->
                // Limpiar listas previas
                topPlatillosList.clear()
                poblanosList.clear()
                postresList.clear()

                for (document in result) {
                    // Obtener los datos del platillo
                    val categoria = document.getString("categoria") ?: "Sin categoría"

                    // Clasificar por categoría
                    when (categoria) {
                        "Poblanos" -> poblanosList.add(document)
                        "Postres" -> postresList.add(document)
                    }

                    // Agregar a la lista general
                    topPlatillosList.add(document)
                }

                // Notificar a los adaptadores
                adapterTopPlatillos.notifyDataSetChanged()
                adapterPoblanos.notifyDataSetChanged()
                adapterPostres.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los platillos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
