package com.example.app_api.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.app_api.LoginRequest
import com.example.app_api.LoginResponse
import com.example.app_api.R
import com.example.app_api.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario: EditText
    private lateinit var etContrasena: EditText
    private lateinit var btnIngresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Ocultar la barra de título
        supportActionBar?.hide()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        } else {
            // Para versiones anteriores a Android 6.0 (Marshmallow)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        }
        // Inicializar vistas
        etUsuario = findViewById(R.id.etUsuario)
        etContrasena = findViewById(R.id.etContrasena)
        btnIngresar = findViewById(R.id.btnIngresar)

        // Configurar el botón de inicio de sesión
        btnIngresar.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString().trim()

            if (validarCampos(usuario, contrasena)) {
                // Sanitizar entradas antes de enviarlas
                val usuarioSanitizado = sanitizarEntrada(usuario)
                val contrasenaSanitizada = sanitizarEntrada(contrasena)

                iniciarSesion(usuarioSanitizado, contrasenaSanitizada)
            }
        }
    }

    private fun validarCampos(usuario: String, contrasena: String): Boolean {
        return when {
            usuario.isEmpty() -> {
                mostrarError("Por favor, ingresa tu usuario.")
                false
            }
            contrasena.isEmpty() -> {
                mostrarError("Por favor, ingresa tu contraseña.")
                false
            }
            contrasena.length < 6 -> {
                mostrarError("La contraseña debe tener al menos 6 caracteres.")
                false
            }
            !esEntradaSegura(usuario) || !esEntradaSegura(contrasena) -> {
                mostrarError("Entrada no válida. Evita usar caracteres especiales.")
                false
            }
            else -> true
        }
    }

    private fun esEntradaSegura(entrada: String): Boolean {
        // Expresión regular para permitir solo letras, números y algunos caracteres seguros
        val regex = "^[a-zA-Z0-9@]*$"
        return entrada.matches(regex.toRegex())
    }

    private fun sanitizarEntrada(entrada: String): String {
        // Eliminar caracteres potencialmente peligrosos
        return entrada.replace("[<>\"'\\/;=]".toRegex(), "")
    }

    private fun iniciarSesion(username: String, password: String) {
        val request = LoginRequest(username, password)
        val call = RetrofitClient.authApi.login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.access_token
                    if (!token.isNullOrEmpty()) {
                        mostrarExito(token)
                    } else {
                        mostrarError("Token vacío o nulo. Revisa la API.")
                    }
                } else {
                    // Manejar errores de la API
                    val errorBody = response.errorBody()?.string()
                    val mensajeError = if (!errorBody.isNullOrEmpty()) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            val detail = jsonObject.getString("detail")

                            when {
                                detail.contains("usuario", ignoreCase = true) -> {
                                    "Usuario incorrecto. Verifica tu nombre de usuario."
                                }
                                detail.contains("contraseña", ignoreCase = true) -> {
                                    "Contraseña incorrecta. Verifica tu contraseña."
                                }
                                else -> detail
                            }
                        } catch (e: Exception) {
                            "Error en la respuesta: $errorBody"
                        }
                    } else {
                        "Error en la respuesta: ${response.code()}"
                    }
                    mostrarError(mensajeError)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mostrarError("Error de conexión. Verifica tu conexión a internet.")
            }
        })
    }

    private fun mostrarExito(token: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("¡Bienvenido!")
            .setContentText("Inicio de sesión exitoso")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                navegarAWelcomeActivity(token)
            }
            .show()
    }

    private fun navegarAWelcomeActivity(token: String) {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
        limpiarCampos()
    }

    private fun mostrarError(mensaje: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(mensaje)
            .setConfirmText("OK")
            .show()
    }

    private fun limpiarCampos() {
        etUsuario.text.clear()
        etContrasena.text.clear()
    }
}