package com.example.app_api.microservicio_nuevo

data class ProveedorResponse(
    val mensaje: String,
    val proveedores: List<Proveedor>
)