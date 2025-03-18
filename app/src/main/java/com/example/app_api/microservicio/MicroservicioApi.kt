package com.example.app_api.microservicio_nuevo

import retrofit2.Call
import retrofit2.http.*

interface MicroservicioApi {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    // Obtener todos los proveedores
    @GET("proveedores")
    fun obtenerProveedores(@Header("Authorization") token: String): Call<ProveedorResponse>

    // Obtener un proveedor específico por ID
    @GET("proveedores/{id}")
    fun obtenerProveedor(
        @Header("Authorization") token: String,
        @Path("id") proveedorId: String
    ): Call<Proveedor>

    @POST("proveedores")
    fun insertarProveedor(
        @Header("Authorization") token: String,
        @Body proveedor: Proveedor
    ): Call<Proveedor>

    @PUT("proveedores/{id}")
    fun actualizarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body proveedor: Proveedor
    ): Call<Proveedor>

    @DELETE("proveedores/{id}")
    fun eliminarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}
