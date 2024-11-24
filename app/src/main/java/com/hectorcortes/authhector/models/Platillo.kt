package com.hectorcortes.authhector.models

data class Platillo(
    var id: String = "",            // ID del platillo, inicializado como una cadena vacía
    val nombre: String = "",        // Nombre del platillo
    val descripcion: String = "",   // Descripción del platillo
    val ingredientes: String = "",  // Ingredientes del platillo
    val pasos: String = "",         // Pasos para preparar el platillo
    val categoria: String = "",     // Categoría del platillo (e.g., "Postres", "Sopas")
    val imagenUri: String = ""      // URI de la imagen almacenada
)
