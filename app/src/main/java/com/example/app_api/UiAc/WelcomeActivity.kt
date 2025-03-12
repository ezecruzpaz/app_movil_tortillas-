package com.example.app_api.ui


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
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
        tvBienvenida.visibility = View.GONE // <-- Ocultar el mensaje de bienvenida

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Iniciar sesión y cargar proveedores
        iniciarSesionMicroservicio() // <-- Esto cargará los proveedores automáticamente
    }

    private fun iniciarSesionMicroservicio() {
        val request = LoginRequest("admin", "123456")
        val call = MicroservicioRetrofitClient.instance.login(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    token = response.body()?.access_token
                    if (!token.isNullOrEmpty()) {
                        obtenerProveedores(token!!) // <-- Cargar proveedores después del login
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
            R.id.nav_actualizar -> {
                abrirActualizarProveedor()
            }
        }

        // Cerrar el menú lateral después de seleccionar una opción
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun obtenerProveedores(token: String) {
        val call = MicroservicioRetrofitClient.instance.obtenerProveedores("Bearer $token")

        call.enqueue(object : Callback<ProveedorResponse> {
            override fun onResponse(call: Call<ProveedorResponse>, response: Response<ProveedorResponse>) {
                if (response.isSuccessful) {
                    val proveedores = response.body()?.proveedores
                    if (proveedores != null && proveedores.isNotEmpty()) {
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
        val adapter = ProveedorAdapter(proveedores) { proveedor ->
            // Usar !! para convertir Int? a Int
            eliminarProveedor(proveedor.id_proveedor!!)
        }
        recyclerView.adapter = adapter
    }
    private fun abrirInsertarProveedor() {
        val intent = Intent(this, InsertarProveedorActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    private fun abrirActualizarProveedor() {
        val intent = Intent(this, ActualizarProveedorActivity::class.java)
        intent.putExtra("TOKEN", token)
        startActivity(intent)
    }

    private fun eliminarProveedor(id: Int) {
        token?.let {
            val call = MicroservicioRetrofitClient.instance.eliminarProveedor("Bearer $it", id)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        mostrarError("Proveedor eliminado correctamente")
                        obtenerProveedores(it) // Refrescar la lista
                    } else {
                        mostrarError("Error al eliminar: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    mostrarError("Error de conexión: ${t.message}")
                }
            })
        } ?: mostrarError("Error: No se recibió el token.")
    }

    private fun mostrarError(mensaje: String) {
        tvBienvenida.text = mensaje
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}