package com.example.app_api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

fun interface AuthApi {
    @POST("token/")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
