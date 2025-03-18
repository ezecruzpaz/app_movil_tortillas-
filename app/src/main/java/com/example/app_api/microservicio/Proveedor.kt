package com.example.app_api.microservicio_nuevo

import java.io.Serializable

data class Proveedor(
    val id_proveedor: String? = null,  // Cambiado a String?
    val nombre_proveedor: String,
    val rfc: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val contacto: String,
    val producto_principal: String,
    val estado: String
) : Serializable
