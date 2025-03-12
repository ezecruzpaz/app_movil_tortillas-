package com.example.app_api.microservicio_nuevo

data class Proveedor(
    val id_proveedor: Int? = null,
    val nombre_proveedor: String,
    val rfc: String,
    val direccion: String,
    val telefono: String,
    val email: String,
    val contacto: String,
    val producto_principal: String,
    val estado: String
)