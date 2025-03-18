package com.example.app_api.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import cn.pedant.SweetAlert.SweetAlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActualizarProveedorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var etNombre: EditText
    private lateinit var etRFC: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContacto: EditText
    private lateinit var etProductoPrincipal: EditText
    private lateinit var etEstado: EditText
    private lateinit var btnActualizar: Button
    private lateinit var token: String
    private lateinit var proveedorId: String


    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_proveedor)

        // Inicializar DrawerLayout y NavigationView
        // Inicializar el DrawerLayout y NavigationView
        // Inicializar el DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configurar el NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Configurar el listener para los ítems del menú de navegación
        navigationView.setNavigationItemSelectedListener(this)

        // Obtener los datos del Intent
        val proveedor = intent.getSerializableExtra("PROVEEDOR") as? Proveedor
        token = intent.getStringExtra("TOKEN") ?: ""

        if (proveedor != null) {
            proveedorId = proveedor.id_proveedor ?: ""
            Log.d("ActualizarProveedor", "Proveedor ID recibido: $proveedorId")

            // Inicializar los campos
            etNombre = findViewById(R.id.etNombre)
            etRFC = findViewById(R.id.etRFC)
            etDireccion = findViewById(R.id.etDireccion)
            etTelefono = findViewById(R.id.etTelefono)
            etEmail = findViewById(R.id.etEmail)
            etContacto = findViewById(R.id.etContacto)
            etProductoPrincipal = findViewById(R.id.etProductoPrincipal)
            etEstado = findViewById(R.id.etEstado)
            btnActualizar = findViewById(R.id.btnActualizar)

            // Mostrar los datos del proveedor en los campos
            etNombre.setText(proveedor.nombre_proveedor)
            etRFC.setText(proveedor.rfc)
            etDireccion.setText(proveedor.direccion)
            etTelefono.setText(proveedor.telefono)
            etEmail.setText(proveedor.email)
            etContacto.setText(proveedor.contacto)
            etProductoPrincipal.setText(proveedor.producto_principal)
            etEstado.setText(proveedor.estado)
        } else {
            Toast.makeText(this, "Proveedor no encontrado", Toast.LENGTH_SHORT).show()
            finish()  // Cierra la actividad si no hay proveedor
        }

        // Configurar el botón de actualización
        btnActualizar.setOnClickListener {
            if (proveedorId.isNotEmpty()) {
                actualizarProveedor(proveedorId)
            } else {
                Toast.makeText(this, "Proveedor no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener los datos del proveedor
    private fun obtenerProveedor(proveedorId: String) {
        val call = MicroservicioRetrofitClient.instance.obtenerProveedor("Bearer $token", proveedorId)

        call.enqueue(object : Callback<Proveedor> {
            override fun onResponse(call: Call<Proveedor>, response: Response<Proveedor>) {
                if (response.isSuccessful) {
                    val proveedor = response.body()
                    if (proveedor != null) {
                        Log.d("ActualizarProveedor", "Proveedor recibido: $proveedor")
                        // Mostrar los datos del proveedor en los campos
                        etNombre.setText(proveedor.nombre_proveedor)
                        etRFC.setText(proveedor.rfc)
                        etDireccion.setText(proveedor.direccion)
                        etTelefono.setText(proveedor.telefono)
                        etEmail.setText(proveedor.email)
                        etContacto.setText(proveedor.contacto)
                        etProductoPrincipal.setText(proveedor.producto_principal)
                        etEstado.setText(proveedor.estado)
                    } else {
                        Log.e("ActualizarProveedor", "Proveedor no encontrado en la respuesta")
                        Toast.makeText(this@ActualizarProveedorActivity, "Proveedor no encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("ActualizarProveedor", "Respuesta no exitosa: ${response.code()}")
                    Toast.makeText(this@ActualizarProveedorActivity, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                Log.e("ActualizarProveedor", "Error de conexión", t)
                Toast.makeText(this@ActualizarProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para actualizar el proveedor
    private fun actualizarProveedor(proveedorId: String) {
        // Validar los campos
        val nombre = etNombre.text.toString()
        val rfc = etRFC.text.toString()
        val direccion = etDireccion.text.toString()
        val telefono = etTelefono.text.toString()
        val email = etEmail.text.toString()
        val contacto = etContacto.text.toString()
        val productoPrincipal = etProductoPrincipal.text.toString()
        val estado = etEstado.text.toString()

        if (nombre.isEmpty() || rfc.isEmpty() || direccion.isEmpty() || telefono.isEmpty() || email.isEmpty() || contacto.isEmpty() || productoPrincipal.isEmpty() || estado.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show()
            return
        }

        val proveedorActualizado = Proveedor(
            nombre_proveedor = nombre,
            rfc = rfc,
            direccion = direccion,
            telefono = telefono,
            email = email,
            contacto = contacto,
            producto_principal = productoPrincipal,
            estado = estado
        )

        val call = MicroservicioRetrofitClient.instance.actualizarProveedor(
            "Bearer $token", proveedorId, proveedorActualizado
        )

        call.enqueue(object : Callback<Proveedor> {
            override fun onResponse(call: Call<Proveedor>, response: Response<Proveedor>) {
                if (response.isSuccessful) {
                    val proveedor = response.body()
                    if (proveedor != null) {
                        // Mostrar mensaje de éxito
                        SweetAlertDialog(this@ActualizarProveedorActivity, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Proveedor actualizado con éxito")
                            .setConfirmClickListener {
                                finish()  // Cierra la actividad después de actualizar
                            }
                            .show()
                    } else {
                        // Mostrar mensaje de error si el proveedor es nulo
                        SweetAlertDialog(this@ActualizarProveedorActivity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error al actualizar proveedor")
                            .setContentText("El proveedor no pudo ser actualizado")
                            .show()
                    }
                } else {
                    // Mostrar mensaje de error si la respuesta no es exitosa
                    SweetAlertDialog(this@ActualizarProveedorActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error al actualizar proveedor")
                        .setContentText("Código de error: ${response.code()}")
                        .show()
                }
            }

            override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                // Mostrar mensaje de error de conexión
                SweetAlertDialog(this@ActualizarProveedorActivity, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Error de conexión")
                    .setContentText("No se pudo conectar al servidor")
                    .show()
            }
        })
    }

    // Implementación de la interfaz NavigationView.OnNavigationItemSelectedListener
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_insertar -> {
                val intent = Intent(this, InsertarProveedorActivity::class.java)
                intent.putExtra("TOKEN", token)
                startActivity(intent)
            }
            R.id.nav_mostrar -> {
                val intent = Intent(this, WelcomeActivity::class.java)
                intent.putExtra("TOKEN", token)
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