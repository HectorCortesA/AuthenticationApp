package com.hectorcortes.authhector

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
            onClick(platillo)
        }
    }

    override fun getItemCount(): Int = platillosList.size

    class PlatilloViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPlatillo: ImageView = itemView.findViewById(R.id.imageViewPlatillo)
        val nombrePlatillo: TextView = itemView.findViewById(R.id.textViewNombrePlatillo)
        val buttonVerReceta: Button = itemView.findViewById(R.id.buttonVerReceta)
    }
}
