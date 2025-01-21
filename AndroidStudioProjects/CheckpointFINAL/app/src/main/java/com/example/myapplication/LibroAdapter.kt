package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class LibroAdapter(
    private val context: Context,
    private val libros: List<Map<String, Any>>,
    private val showDeleteButton: Boolean = false,
    private val onItemClick: (Map<String, Any>) -> Unit,
    private val onDeleteClick: ((Map<String, Any>) -> Unit)? = null
) : BaseAdapter() {

    override fun getCount(): Int = libros.size
    override fun getItem(position: Int): Any = libros[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_libro, parent, false)

        val libro = libros[position]

        val tvNombreLibro = view.findViewById<TextView>(R.id.tvNombreLibro)
        val tvDetalleLibro = view.findViewById<TextView>(R.id.tvDetalleLibro)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvNombreLibro.text = "${libro["titulo"]} - ${libro["autor"]}"
        tvDetalleLibro.text = buildString {
            append("Precio: $${libro["precio"]}")
            append(" | ")
            append(if (libro["disponible"] as Int == 1) "Disponible" else "No disponible")
            append(" | ")
            append("Publicado: ${libro["fecha_publicacion"]}")
        }

        btnDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

        view.setOnClickListener { onItemClick(libro) }
        btnDelete.setOnClickListener { 
            if (showDeleteButton) {
                onDeleteClick?.invoke(libro)
            }
        }

        return view
    }
}