package com.example.app_api.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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



        private lateinit var etId: EditText
        private lateinit var etNombre: EditText
        private lateinit var etRFC: EditText
        private lateinit var etDireccion: EditText
        private lateinit var etTelefono: EditText
        private lateinit var etEmail: EditText
        private lateinit var etContacto: EditText
        private lateinit var etProductoPrincipal: EditText
        private lateinit var etEstado: EditText
        private lateinit var btnActualizar: Button
        private lateinit var drawerLayout: DrawerLayout

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_actualizar_proveedor)

            // Inicializar vistas
            etId = findViewById(R.id.etId)
            etNombre = findViewById(R.id.etNombre)
            etRFC = findViewById(R.id.etRFC)
            etDireccion = findViewById(R.id.etDireccion)
            etTelefono = findViewById(R.id.etTelefono)
            etEmail = findViewById(R.id.etEmail)
            etContacto = findViewById(R.id.etContacto)
            etProductoPrincipal = findViewById(R.id.etProductoPrincipal)
            etEstado = findViewById(R.id.etEstado)
            btnActualizar = findViewById(R.id.btnActualizar)
            drawerLayout = findViewById(R.id.drawerLayout)

            // Prevenir el ENTER en los campos de texto

            preventEnterKey(etId, etNombre, etRFC, etDireccion, etTelefono, etEmail, etContacto, etProductoPrincipal, etEstado)

            // Configurar el botón de actualizar
            btnActualizar.setOnClickListener {
                if (validarFormulario()) {
                    actualizarProveedor()
                }
            }

            // Configurar el NavigationView
            val navigationView: NavigationView = findViewById(R.id.navigationView)
            navigationView.setNavigationItemSelectedListener(this)
        }

        private fun preventEnterKey(vararg editTexts: EditText) {
            for (editText in editTexts) {
                editText.setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        // Evita que se procese el ENTER
                        true
                    } else {
                        false
                    }
                }
            }
        }

        private fun validarFormulario(): Boolean {
            var valido = true

            // Validar ID
            if (etId.text.toString().trim().isEmpty()) {
                etId.error = "El ID es obligatorio"
                valido = false
            }

            // Validar Nombre
            if (etNombre.text.toString().trim().isEmpty()) {
                etNombre.error = "El nombre es obligatorio"
                valido = false
            }

            // Validar RFC
            if (etRFC.text.toString().trim().isEmpty()) {
                etRFC.error = "El RFC es obligatorio"
                valido = false
            }

            // Validar Dirección
            if (etDireccion.text.toString().trim().isEmpty()) {
                etDireccion.error = "La dirección es obligatoria"
                valido = false
            }

            // Validar Teléfono
            if (etTelefono.text.toString().trim().isEmpty()) {
                etTelefono.error = "El teléfono es obligatorio"
                valido = false
            } else if (!android.util.Patterns.PHONE.matcher(etTelefono.text.toString()).matches()) {
                etTelefono.error = "Teléfono inválido"
                valido = false
            }

            // Validar Email
            if (etEmail.text.toString().trim().isEmpty()) {
                etEmail.error = "El email es obligatorio"
                valido = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
                etEmail.error = "Email inválido"
                valido = false
            }

            // Validar Contacto
            if (etContacto.text.toString().trim().isEmpty()) {
                etContacto.error = "El contacto es obligatorio"
                valido = false
            }

            // Validar Producto Principal
            if (etProductoPrincipal.text.toString().trim().isEmpty()) {
                etProductoPrincipal.error = "El producto principal es obligatorio"
                valido = false
            }

            // Validar Estado
            if (etEstado.text.toString().trim().isEmpty()) {
                etEstado.error = "El estado es obligatorio"
                valido = false
            }

            return valido
        }

        private fun actualizarProveedor() {
            val id = etId.text.toString().toIntOrNull()
            val nombre = etNombre.text.toString()
            val rfc = etRFC.text.toString()
            val direccion = etDireccion.text.toString()
            val telefono = etTelefono.text.toString()
            val email = etEmail.text.toString()
            val contacto = etContacto.text.toString()
            val productoPrincipal = etProductoPrincipal.text.toString()
            val estado = etEstado.text.toString()

            val proveedor = Proveedor(
                nombre_proveedor = nombre,
                rfc = rfc,
                direccion = direccion,
                telefono = telefono,
                email = email,
                contacto = contacto,
                producto_principal = productoPrincipal,
                estado = estado
            )

            val token = intent.getStringExtra("TOKEN")
            token?.let {
                val call = MicroservicioRetrofitClient.instance.actualizarProveedor("Bearer $it", id!!, proveedor)
                call.enqueue(object : Callback<Proveedor> {
                    override fun onResponse(call: Call<Proveedor>, response: Response<Proveedor>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ActualizarProveedorActivity, "Proveedor actualizado", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@ActualizarProveedorActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                        Toast.makeText(this@ActualizarProveedorActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                    }
                })
            } ?: Toast.makeText(this, "Error: No se recibió el token", Toast.LENGTH_SHORT).show()
        }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.nav_insertar -> {
                    val intent = Intent(this, InsertarProveedorActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_mostrar -> {
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_actualizar -> {
                    // Ya estamos en la actividad de actualizar
                }
            }
            drawerLayout.closeDrawers()
            return true
        }
    }