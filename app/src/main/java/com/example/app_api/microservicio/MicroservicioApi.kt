package com.example.app_api.microservicio_nuevo

import retrofit2.Call
import retrofit2.http.*

interface MicroservicioApi {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("proveedores")
    fun obtenerProveedores(@Header("Authorization") token: String): Call<ProveedorResponse>

    @POST("proveedores")
    fun insertarProveedor(
        @Header("Authorization") token: String,
        @Body proveedor: Proveedor
    ): Call<Proveedor>

    @PUT("proveedores/{id}")
    fun actualizarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body proveedor: Proveedor
    ): Call<Proveedor>

    @DELETE("proveedores/{id}")
    fun eliminarProveedor(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<Void>
}