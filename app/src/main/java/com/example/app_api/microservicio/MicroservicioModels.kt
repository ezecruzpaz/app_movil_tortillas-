package com.example.app_api.microservicio_nuevo

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val accessToken: String
)
