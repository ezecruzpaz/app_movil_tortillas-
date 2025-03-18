package com.example.app_api.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app_api.R
import com.example.app_api.UiAc.ProveedorAdapter
import com.example.app_api.microservicio_nuevo.LoginRequest
import com.example.app_api.microservicio_nuevo.LoginResponse
import com.example.app_api.microservicio_nuevo.MicroservicioRetrofitClient
import com.example.app_api.microservicio_nuevo.Proveedor
import com.example.app_api.microservicio_nuevo.ProveedorResponse
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import cn.pedant.SweetAlert.SweetAlertDialog

class WelcomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var tvBienvenida: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()

        // Configurar la barra de estado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        } else {
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        }

        // Inicializar vistas
        tvBienvenida = findViewById(R.id.tvBienvenida)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        recyclerView = findViewById(R.id.rvProveedores)

        // Ocultar el TextView de bienvenida
        tvBienvenida.visibility = View.GONE

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Iniciar sesión y cargar proveedores
        iniciarSesionMicroservicio()
    }

    private fun iniciarSesionMicroservicio() {
        val request = LoginRequest("admin", "123456")
        val call = MicroservicioRetrofitClient.instance.login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    token = response.body()?.accessToken
                    if (!token.isNullOrEmpty()) {
                        obtenerProveedores(token!!)
                    } else {
                        mostrarError("Token vacío o nulo")
                    }
                } else {
                    mostrarError("Error en el login: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                mostrarError("Error de conexión: ${t.message}")
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_insertar -> {
                abrirInsertarProveedor()
            }
            R.id.nav_mostrar -> {
                token?.let {
                    obtenerProveedores(it)
                } ?: mostrarError("Error: No se recibió el token.")
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun obtenerProveedores(token: String) {
        val call = MicroservicioRetrofitClient.instance.obtenerProveedores("Bearer $token")
        call.enqueue(object : Callback<ProveedorResponse> {
            override fun onResponse(call: Call<ProveedorResponse>, response: Response<ProveedorResponse>) {
                if (response.isSuccessful) {
                    val proveedores = response.body()?.proveedores
                    if (!proveedores.isNullOrEmpty()) {
                        mostrarProveedores(proveedores)
                    } else {
                        mostrarError("No se encontraron proveedores.")
                    }
                } else {
                    mostrarError("Error en la respuesta: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ProveedorResponse>, t: Throwable) {
                mostrarError("Error de conexión: ${t.message}")
            }
        })
    }


    private fun mostrarProveedores(proveedores: List<Proveedor>) {
        val adapter = ProveedorAdapter(
            proveedores = proveedores,
            onEliminarClickListener = { proveedor ->
                eliminarProveedor(proveedor.id_proveedor!!)
            },
            onActualizarClickListener = { proveedor ->
                abrirFormularioActualizar(proveedor)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun abrirFormularioActualizar(proveedor: Proveedor) {
        val intent = Intent(this, ActualizarProveedorActivity::class.java).apply {
            putExtra("PROVEEDOR", proveedor)  // Pasa el objeto completo de proveedor
            putExtra("TOKEN", token)  // Pasa el token
        }
        startActivity(intent)
    }


    private fun abrirInsertarProveedor() {
        val intent = Intent(this, InsertarProveedorActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    private fun eliminarProveedor(id: String) {
        token?.let { token ->
            val idProveedorInt = id.toIntOrNull()

            if (idProveedorInt != null) {
                // Primer SweetAlertDialog: Confirmación de eliminación
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("¿Eliminar proveedor?")
                    .setContentText("¿Deseas eliminar este proveedor?")
                    .setConfirmText("Eliminar")
                    .setCancelText("Cancelar")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation() // Cierra el diálogo de confirmación

                        // Llamada a la API para eliminar
                        val call = MicroservicioRetrofitClient.instance.eliminarProveedor("Bearer $token", idProveedorInt)
                        call.enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                if (response.isSuccessful) {
                                    // Segundo SweetAlertDialog: Éxito al eliminar
                                    SweetAlertDialog(this@WelcomeActivity, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Eliminado")
                                        .setContentText("Proveedor eliminado correctamente")
                                        .setConfirmClickListener { successDialog ->
                                            successDialog.dismissWithAnimation() // Cierra el diálogo de éxito
                                            obtenerProveedores(token) // Actualiza la lista de proveedores
                                        }
                                        .show()
                                } else {
                                    // Segundo SweetAlertDialog: Error al eliminar
                                    SweetAlertDialog(this@WelcomeActivity, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText("Error al eliminar: ${response.errorBody()?.string()}")
                                        .setConfirmClickListener { errorDialog ->
                                            errorDialog.dismissWithAnimation() // Cierra el diálogo de error
                                        }
                                        .show()
                                }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                // Segundo SweetAlertDialog: Error de conexión
                                SweetAlertDialog(this@WelcomeActivity, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error de conexión")
                                    .setContentText(t.message)
                                    .setConfirmClickListener { errorDialog ->
                                        errorDialog.dismissWithAnimation() // Cierra el diálogo de error
                                    }
                                    .show()
                            }
                        })
                    }
                    .setCancelClickListener { dialog ->
                        dialog.dismissWithAnimation() // Cierra el diálogo si el usuario cancela
                    }
                    .show()
            } else {
                // SweetAlertDialog: ID no válido
                SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error")
                    .setContentText("ID de proveedor no válido")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation() // Cierra el diálogo
                    }
                    .show()
            }
        } ?: run {
            // SweetAlertDialog: Token no recibido
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText("No se recibió el token")
                .setConfirmClickListener { dialog ->
                    dialog.dismissWithAnimation() // Cierra el diálogo
                }
                .show()
        }
    }
    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
