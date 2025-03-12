package com.example.app_api.microservicio_nuevo

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MicroservicioRetrofitClient {
    private const val BASE_URL = "http://3.145.42.54:4000/"

    val instance: MicroservicioApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MicroservicioApi::class.java)
    }
}