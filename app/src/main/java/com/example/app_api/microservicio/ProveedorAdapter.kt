package com.example.app_api.UiAc

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.app_api.R
import com.example.app_api.microservicio_nuevo.Proveedor

class ProveedorAdapter(
    private val proveedores: List<Proveedor>,
    private val onEliminarClickListener: (Proveedor) -> Unit, // Función lambda para manejar la eliminación
    private val onActualizarClickListener: (Proveedor) -> Unit // Nueva función lambda para manejar la actualización
) : RecyclerView.Adapter<ProveedorAdapter.ProveedorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProveedorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_proveedor, parent, false)
        return ProveedorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProveedorViewHolder, position: Int) {
        val proveedor = proveedores[position]
        holder.bind(proveedor)
    }

    override fun getItemCount(): Int = proveedores.size

    inner class ProveedorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        private val tvRFC: TextView = itemView.findViewById(R.id.tvRFC)
        private val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        private val tvTelefono: TextView = itemView.findViewById(R.id.tvTelefono)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvContacto: TextView = itemView.findViewById(R.id.tvContacto)
        private val tvProductoPrincipal: TextView = itemView.findViewById(R.id.tvProductoPrincipal)
        private val tvEstado: TextView = itemView.findViewById(R.id.tvEstado)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)
        private val btnActualizar: ImageButton = itemView.findViewById(R.id.btnActualizar) // Nuevo botón de actualizar

        fun bind(proveedor: Proveedor) {
            tvNombre.text = "Nombre: ${proveedor.nombre_proveedor}"
            tvRFC.text = "RFC: ${proveedor.rfc}"
            tvDireccion.text = "Dirección: ${proveedor.direccion}"
            tvTelefono.text = "Teléfono: ${proveedor.telefono}"
            tvEmail.text = "Email: ${proveedor.email}"
            tvContacto.text = "Contacto: ${proveedor.contacto}"
            tvProductoPrincipal.text = "Producto Principal: ${proveedor.producto_principal}"
            tvEstado.text = "Estado: ${proveedor.estado}"

            // Configurar el clic del botón de eliminar
            btnEliminar.setOnClickListener {
                mostrarDialogoConfirmacion(proveedor, itemView.context)
            }

            // Configurar el clic del botón de actualizar
            btnActualizar.setOnClickListener {
                onActualizarClickListener(proveedor) // Llamar a la función de actualización
            }
        }

        private fun mostrarDialogoConfirmacion(proveedor: Proveedor, context: Context) {
            AlertDialog.Builder(context)
                .setTitle("Eliminar Proveedor")
                .setMessage("¿Deseas eliminar este proveedor?")
                .setPositiveButton("Sí") { _, _ ->
                    onEliminarClickListener(proveedor) // Llamar a la función de eliminación
                }
                .setNegativeButton("No", null)
                .show()
        }
    }
}