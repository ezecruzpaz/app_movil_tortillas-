package com.example.app_api.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.app_api.R
import com.example.app_api.microservicio_nuevo.MicroservicioRetrofitClient
import com.example.app_api.microservicio_nuevo.Proveedor
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InsertarProveedorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var etNombre: EditText
    private lateinit var etRFC: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContacto: EditText
    private lateinit var etProductoPrincipal: EditText
    private lateinit var etEstado: EditText
    private lateinit var btnGuardar: Button

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insertar_proveedor)

        // Inicializar vistas
        etNombre = findViewById(R.id.etNombre)
        etRFC = findViewById(R.id.etRFC)
        etDireccion = findViewById(R.id.etDireccion)
        etTelefono = findViewById(R.id.etTelefono)
        etEmail = findViewById(R.id.etEmail)
        etContacto = findViewById(R.id.etContacto)
        etProductoPrincipal = findViewById(R.id.etProductoPrincipal)
        etEstado = findViewById(R.id.etEstado)
        btnGuardar = findViewById(R.id.btnGuardar)

        // Inicializar el DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configurar el NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener {
            val proveedor = Proveedor(
                nombre_proveedor = etNombre.text.toString(),
                rfc = etRFC.text.toString(),
                direccion = etDireccion.text.toString(),
                telefono = etTelefono.text.toString(),
                email = etEmail.text.toString(),
                contacto = etContacto.text.toString(),
                producto_principal = etProductoPrincipal.text.toString(),
                estado = etEstado.text.toString()
            )

            insertarProveedor(proveedor)
        }
    }

    private fun insertarProveedor(proveedor: Proveedor) {
        val token = intent.getStringExtra("TOKEN")
        token?.let {
            val call = MicroservicioRetrofitClient.instance.insertarProveedor("Bearer $it", proveedor)
            call.enqueue(object : Callback<Proveedor> {
                override fun onResponse(call: Call<Proveedor>, response: Response<Proveedor>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@InsertarProveedorActivity, "Proveedor insertado", Toast.LENGTH_SHORT).show()
                        finish() // Cerrar la actividad
                    } else {
                        Toast.makeText(this@InsertarProveedorActivity, "Error al insertar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                    Toast.makeText(this@InsertarProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })
        } ?: Toast.makeText(this, "Error: No se recibió el token", Toast.LENGTH_SHORT).show()
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_insertar -> {
                // Ya estamos en la actividad de insertar
            }
            R.id.nav_mostrar -> {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_actualizar -> {
                val intent = Intent(this, ActualizarProveedorActivity::class.java)
                startActivity(intent)
            }

        }

        // Cerrar el menú lateral después de seleccionar una opción
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Cerrar el menú lateral si está abierto
        } else {
            super.onBackPressed()
        }
    }
}