package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class LibroAdapter(
    private val context: Context,
    private val libros: List<Map<String, Any>>,
    private val onItemClick: (Map<String, Any>) -> Unit
) : BaseAdapter() {

    private class ViewHolder(
        val titulo: TextView,
        val detalle: TextView
    )

    override fun getCount(): Int = libros.size

    override fun getItem(position: Int): Any = libros[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)
            holder = ViewHolder(
                view.findViewById(android.R.id.text1),
                view.findViewById(android.R.id.text2)
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val libro = libros[position]
        holder.titulo.text = "${libro["titulo"]} - ${libro["autor"]}"
        holder.detalle.text = buildString {
            append("Precio: $${libro["precio"]}")
            append(" | ")
            append(if (libro["disponible"] as Int == 1) "Disponible" else "No disponible")
            append(" | ")
            append("Publicado: ${libro["fecha_publicacion"]}")
        }

        view.setOnClickListener {
            onItemClick(libro)
        }

        return view
    }
}