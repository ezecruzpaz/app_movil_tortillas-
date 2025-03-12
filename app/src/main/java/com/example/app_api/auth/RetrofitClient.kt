package com.example.app_api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://fastapiprueba.onrender.com/")  // Asegúrate de que la URL esté bien
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    val authApi: AuthApi by lazy {  // ✅ Agregamos la API de autenticación
        retrofit.create(AuthApi::class.java)
    }
}