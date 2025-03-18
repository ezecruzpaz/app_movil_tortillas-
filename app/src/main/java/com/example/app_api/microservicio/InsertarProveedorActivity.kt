package com.example.app_api.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
import cn.pedant.SweetAlert.SweetAlertDialog
import java.util.regex.Pattern

class InsertarProveedorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var etNombre: EditText
    private lateinit var etRFC: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etEmail: EditText
    private lateinit var etContacto: EditText
    private lateinit var etProductoPrincipal: EditText
    private lateinit var spinnerEstado: Spinner // Cambiar a Spinner
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
        spinnerEstado = findViewById(R.id.spinnerEstado) // Inicializar Spinner
        btnGuardar = findViewById(R.id.btnGuardar)

        // Inicializar el DrawerLayout y NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configurar el NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        // Configurar el Spinner
        configurarSpinnerEstado()

        // Configurar el botón de guardar
        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                val proveedor = Proveedor(
                    nombre_proveedor = etNombre.text.toString(),
                    rfc = etRFC.text.toString(),
                    direccion = etDireccion.text.toString(),
                    telefono = etTelefono.text.toString(),
                    email = etEmail.text.toString(),
                    contacto = etContacto.text.toString(),
                    producto_principal = etProductoPrincipal.text.toString(),
                    estado = spinnerEstado.selectedItem.toString() // Obtener valor del Spinner
                )

                insertarProveedor(proveedor)
            }
        }
    }

    private fun configurarSpinnerEstado() {
        // Opciones para el Spinner
        val opcionesEstado = arrayOf("Activo", "Inactivo ")

        // Adaptador para el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesEstado)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        spinnerEstado.adapter = adapter
    }

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

    private fun insertarProveedor(proveedor: Proveedor) {
        val token = intent.getStringExtra("TOKEN")
        token?.let {
            val call = MicroservicioRetrofitClient.instance.insertarProveedor("Bearer $it", proveedor)
            call.enqueue(object : Callback<Proveedor> {
                override fun onResponse(call: Call<Proveedor>, response: Response<Proveedor>) {
                    if (response.isSuccessful) {
                        mostrarExito("Proveedor insertado correctamente")
                    } else {
                        mostrarError("Error al insertar el proveedor")
                    }
                }

                override fun onFailure(call: Call<Proveedor>, t: Throwable) {
                    mostrarError("Error de conexión: ${t.message}")
                }
            })
        } ?: mostrarError("Error: No se recibió el token")
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