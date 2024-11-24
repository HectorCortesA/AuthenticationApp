package com.hectorcortes.authhector
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.io.ByteArrayInputStream

class PlatilloAdapter(
    private val platillosList: List<QueryDocumentSnapshot>,
    private val onClick: (QueryDocumentSnapshot) -> Unit
) : RecyclerView.Adapter<PlatilloAdapter.PlatilloViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatilloViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_platillo, parent, false)
        return PlatilloViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlatilloViewHolder, position: Int) {
        val platillo = platillosList[position]
        val nombre = platillo.getString("nombre") ?: "Desconocido"
        val imagenBase64 = platillo.getString("imagenBase64")

        holder.nombrePlatillo.text = nombre

        // Convertir la imagen Base64 a Bitmap
        if (imagenBase64 != null) {
            val imageBytes = Base64.decode(imagenBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
            holder.imageViewPlatillo.setImageBitmap(bitmap)
        }

        holder.buttonVerReceta.setOnClickListener {
            // Crear un Intent para abrir la nueva actividad
            val context = holder.itemView.context
            val intent = Intent(context, DetallePlatilloActivity::class.java).apply {
                putExtra("nombre", nombre)
                putExtra("descripcion", platillo.getString("descripcion") ?: "Descripción no disponible")
                putExtra("ingredientes", platillo.getString("ingredientes") ?: "Ingredientes no disponibles")
                putExtra("pasos", platillo.getString("pasos") ?: "Pasos no disponibles")
                putExtra("imagenBase64", imagenBase64 ?: "")
            }
            context.startActivity(intent)
        }
    }

    private fun putExtra(s: String, nombre: String) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = platillosList.size

    class PlatilloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPlatillo: ImageView = itemView.findViewById(R.id.imageViewPlatillo)
        val nombrePlatillo: TextView = itemView.findViewById(R.id.textViewNombrePlatillo)
        val buttonVerReceta: Button = itemView.findViewById(R.id.buttonVerReceta)
    }
}

