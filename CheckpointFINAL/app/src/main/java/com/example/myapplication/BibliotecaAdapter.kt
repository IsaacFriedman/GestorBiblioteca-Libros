package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView

class BibliotecaAdapter(
    private val context: Context,
    private val bibliotecas: List<Map<String, Any>>,
    private val showDeleteButton: Boolean,
    private val onItemClick: (Map<String, Any>) -> Unit,
    private val onDeleteClick: (Map<String, Any>) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = bibliotecas.size
    override fun getItem(position: Int): Any = bibliotecas[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_biblioteca, parent, false)

        val biblioteca = bibliotecas[position]

        val tvNombreBiblioteca = view.findViewById<TextView>(R.id.tvNombreBiblioteca)
        val tvDetalleBiblioteca = view.findViewById<TextView>(R.id.tvDetalleBiblioteca)
        val btnDelete = view.findViewById<ImageButton>(R.id.btnDelete)

        tvNombreBiblioteca.text = biblioteca["nombre"] as String
        tvDetalleBiblioteca.text = buildString {
            append("Dirección: ${biblioteca["direccion"]}")
            append("\nPresupuesto: $${biblioteca["presupuesto"]}")
            append("\nInaugurada: ${biblioteca["inaugurada"]}")
            append("\nZona: ${biblioteca["zona"]}")
        }

        btnDelete.visibility = if (showDeleteButton) View.VISIBLE else View.GONE

        view.setOnClickListener { onItemClick(biblioteca) }
        btnDelete.setOnClickListener { onDeleteClick(biblioteca) }

        return view
    }
}