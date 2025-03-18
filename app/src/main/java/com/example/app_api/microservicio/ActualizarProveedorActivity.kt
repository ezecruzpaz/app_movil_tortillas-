package com.example.app_api.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import java.util.regex.Pattern

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
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configurar el listener para los ítems del menú de navegación
        navigationView.setNavigationItemSelectedListener(this)

        // Obtener los datos del Intent
        val proveedor = intent.getSerializableExtra("PROVEEDOR") as? Proveedor
        token = intent.getStringExtra("TOKEN") ?: ""

        if (proveedor != null) {
            proveedorId = proveedor.id_proveedor ?: ""
            Log.d("ActualizarProveedor", "Proveedor ID recibido: $proveedorId")

            // Ocultar la barra de título
            supportActionBar?.hide()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                window.statusBarColor = Color.WHITE
            } else {
                // Para versiones anteriores a Android 6.0 (Marshmallow)
                window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
            }
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
                if (validarCampos()) {
                    actualizarProveedor(proveedorId)
                }
            } else {
                Toast.makeText(this, "Proveedor no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para validar los campos
    private fun validarCampos(): Boolean {
        // Validar Nombre
        if (etNombre.text.toString().isEmpty()) {
            mostrarError("El campo Nombre no puede estar vacío")
            return false
        }
        if (!esSoloLetras(etNombre.text.toString())) {
            mostrarError("El campo Nombre solo puede contener letras y espacios")
            return false
        }

        // Validar RFC
        if (etRFC.text.toString().isEmpty()) {
            mostrarError("El campo RFC no puede estar vacío")
            return false
        }
        if (!esRFCValido(etRFC.text.toString())) {
            mostrarError("El campo RFC no tiene un formato válido")
            return false
        }

        // Validar Dirección
        if (etDireccion.text.toString().isEmpty()) {
            mostrarError("El campo Dirección no puede estar vacío")
            return false
        }
        if (!esDireccionValida(etDireccion.text.toString())) {
            mostrarError("El campo Dirección contiene caracteres no permitidos")
            return false
        }

        // Validar Teléfono
        if (etTelefono.text.toString().isEmpty()) {
            mostrarError("El campo Teléfono no puede estar vacío")
            return false
        }
        if (!esTelefonoValido(etTelefono.text.toString())) {
            mostrarError("El campo Teléfono debe tener 10 dígitos y solo números")
            return false
        }

        // Validar Email
        if (etEmail.text.toString().isEmpty()) {
            mostrarError("El campo Email no puede estar vacío")
            return false
        }
        if (!esEmailValido(etEmail.text.toString())) {
            mostrarError("El campo Email no tiene un formato válido")
            return false
        }

        // Validar Contacto
        if (etContacto.text.toString().isEmpty()) {
            mostrarError("El campo Contacto no puede estar vacío")
            return false
        }
        if (!esSoloLetras(etContacto.text.toString())) {
            mostrarError("El campo Contacto solo puede contener letras y espacios")
            return false
        }

        // Validar Producto Principal
        if (etProductoPrincipal.text.toString().isEmpty()) {
            mostrarError("El campo Producto Principal no puede estar vacío")
            return false
        }
        if (!esProductoValido(etProductoPrincipal.text.toString())) {
            mostrarError("El campo Producto Principal contiene caracteres no permitidos")
            return false
        }

        // Validar Estado
        if (etEstado.text.toString().isEmpty()) {
            mostrarError("El campo Estado no puede estar vacío")
            return false
        }

        return true
    }

    // Función para validar que solo contenga letras y espacios
    private fun esSoloLetras(texto: String): Boolean {
        val regex = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+\$"
        return Pattern.matches(regex, texto)
    }

    // Función para validar el RFC
    private fun esRFCValido(rfc: String): Boolean {
        val regex = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9][A-Z0-9][0-9A-Z]\$"
        return Pattern.matches(regex, rfc)
    }

    // Función para validar la dirección
    private fun esDireccionValida(direccion: String): Boolean {
        val regex = "^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ\\s#.,-]+\$"
        return Pattern.matches(regex, direccion)
    }

    // Función para validar el teléfono (10 dígitos)
    private fun esTelefonoValido(telefono: String): Boolean {
        val regex = "^[0-9]{10}\$"
        return Pattern.matches(regex, telefono)
    }

    // Función para validar el email
    private fun esEmailValido(email: String): Boolean {
        val regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        return Pattern.matches(regex, email)
    }

    // Función para validar el producto principal
    private fun esProductoValido(producto: String): Boolean {
        val regex = "^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ\\s#.,-]+\$"
        return Pattern.matches(regex, producto)
    }

    private fun mostrarError(mensaje: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(mensaje)
            .show()
    }

    private fun mostrarExito(mensaje: String) {
        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText("Éxito")
            .setContentText(mensaje)
            .setConfirmClickListener {
                finish() // Cerrar la actividad después de mostrar el mensaje de éxito
            }
            .show()
    }

    // Función para actualizar el proveedor
    private fun actualizarProveedor(proveedorId: String) {
        val nombre = etNombre.text.toString()
        val rfc = etRFC.text.toString()
        val direccion = etDireccion.text.toString()
        val telefono = etTelefono.text.toString()
        val email = etEmail.text.toString()
        val contacto = etContacto.text.toString()
        val productoPrincipal = etProductoPrincipal.text.toString()
        val estado = etEstado.text.toString()

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
                    mostrarExito("Proveedor actualizado con éxito")
                } else {
                    mostrarError("Error al actualizar el proveedor")
                }
            }

            override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                mostrarError("Error de conexión: ${t.message}")
            }
        })
    }

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