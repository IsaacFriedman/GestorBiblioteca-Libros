package com.example.myapplication

import android.app.AlertDialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLibroBinding
import java.text.SimpleDateFormat
import java.util.Locale

class Libro : AppCompatActivity() {
    private lateinit var binding: ActivityLibroBinding
    private lateinit var db: SQLiteDatabase
    private var lId: Int = 0
    private var bId: Int = 0
    private var msg: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ConnectionClass.getConnection(this)

        val i = intent
        msg = i.getStringExtra("msg") ?: ""
        lId = i.getIntExtra("lId", 0)
        bId = i.getIntExtra("bId", 0)

        // Si no es "add", ocultamos spinner de bibliotecas
        // (asumiendo que en este ejemplo "add" sería para crear libro nuevo).
        if (msg != "add") {
            binding.tvLabelBiblioteca.visibility = View.GONE
            binding.spinnerBibliotecas.visibility = View.GONE
        }

        when (msg) {
            "add" -> {
                // Lógica para insertar nuevo libro
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                cargarBibliotecas() // llenamos el spinner
            }
            "edit" -> {
                // Lógica para editar
                binding.btnSave.text = "Actualizar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                cargarDatosLibro()
            }
            "delete" -> {
                // Lógica para eliminar
                binding.btnSave.visibility = View.GONE
                binding.btnDelete.text = "Eliminar"
                binding.btnDelete.visibility = View.VISIBLE
                cargarDatosLibro()
                bloquearCampos()
            }
        }

        binding.btnSave.setOnClickListener {
            when (msg) {
                "add" -> insertData()
                "edit" -> updateData()
            }
        }

        binding.btnDelete.setOnClickListener {
            if (msg == "delete") {
                deleteData()
            }
        }
    }

    private fun cargarBibliotecas() {
        val cursor = db.rawQuery("SELECT id, nombre FROM ${ConnectionClass.TABLE_BIBLIOTECA}", null)
        val bibliotecas = mutableListOf<String>()
        val bibliotecaIds = mutableListOf<Int>()

        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_ID))
                val nombre = it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_NOMBRE))
                bibliotecas.add(nombre)
                bibliotecaIds.add(id)
            }
        }

        if (bibliotecas.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bibliotecas)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerBibliotecas.adapter = adapter

            binding.spinnerBibliotecas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    bId = bibliotecaIds[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    bId = 0
                }
            }
        } else {
            Toast.makeText(this, "No hay bibliotecas disponibles", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun cargarDatosLibro() {
        try {
            // Intentamos cargar datos que vienen en el Intent primero
            if (intent.hasExtra("titulo")) {
                binding.etTituloLibro.setText(intent.getStringExtra("titulo"))
                binding.etAutorLibro.setText(intent.getStringExtra("autor"))
                binding.etPrecioLibro.setText(intent.getDoubleExtra("precio", 0.0).toString())
                binding.cbDisponible.isChecked = (intent.getIntExtra("disponible", 0) == 1)
                binding.etFechaPublicacion.setText(intent.getStringExtra("fecha_publicacion"))
            } else {
                // Si no vienen en el Intent, cargamos de la BD
                val cursor = db.rawQuery(
                    "SELECT * FROM ${ConnectionClass.TABLE_LIBRO} WHERE ${ConnectionClass.COL_LIBRO_ID} = ?",
                    arrayOf(lId.toString())
                )
                cursor.use {
                    if (it.moveToFirst()) {
                        binding.etTituloLibro.setText(it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_TITULO)))
                        binding.etAutorLibro.setText(it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_AUTOR)))
                        binding.etPrecioLibro.setText(
                            it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_PRECIO)).toString()
                        )
                        binding.cbDisponible.isChecked = (it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_DISPONIBLE)) == 1)
                        binding.etFechaPublicacion.setText(
                            it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_FECHA_PUBLICACION))
                        )
                    } else {
                        Toast.makeText(this, "No se encontró el libro con ID: $lId", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar datos del libro: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun bloquearCampos() {
        binding.etTituloLibro.isEnabled = false
        binding.etAutorLibro.isEnabled = false
        binding.etPrecioLibro.isEnabled = false
        binding.cbDisponible.isEnabled = false
        binding.etFechaPublicacion.isEnabled = false
        binding.spinnerBibliotecas.isEnabled = false
    }

    private fun validarCampos(): Boolean {
        // Solo pedimos la biblioteca si estamos en modo "add"
        if (msg == "add" && bId == 0) {
            Toast.makeText(this, "Debe seleccionar una biblioteca", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etTituloLibro.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El título es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etAutorLibro.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El autor es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etPrecioLibro.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El precio es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etFechaPublicacion.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La fecha de publicación es requerida", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validamos la fecha en formato yyyy/MM/dd
        try {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(binding.etFechaPublicacion.text.toString().trim())
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de fecha inválido. Use yyyy/mm/dd", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun insertData() {
        if (!validarCampos()) return

        val values = ContentValues().apply {
            put(ConnectionClass.COL_LIBRO_BIBLIOTECA_ID, bId)
            put(ConnectionClass.COL_LIBRO_TITULO, binding.etTituloLibro.text.toString().trim())
            put(ConnectionClass.COL_LIBRO_AUTOR, binding.etAutorLibro.text.toString().trim())
            put(ConnectionClass.COL_LIBRO_PRECIO, binding.etPrecioLibro.text.toString().toDouble())
            put(ConnectionClass.COL_LIBRO_DISPONIBLE, if (binding.cbDisponible.isChecked) 1 else 0)
            put(ConnectionClass.COL_LIBRO_FECHA_PUBLICACION, binding.etFechaPublicacion.text.toString().trim())
        }

        val newRowId = db.insert(ConnectionClass.TABLE_LIBRO, null, values)
        if (newRowId > 0) {
            Toast.makeText(this, "Libro insertado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al insertar libro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        if (!validarCampos()) return

        val values = ContentValues().apply {
            put(ConnectionClass.COL_LIBRO_TITULO, binding.etTituloLibro.text.toString().trim())
            put(ConnectionClass.COL_LIBRO_AUTOR, binding.etAutorLibro.text.toString().trim())
            put(ConnectionClass.COL_LIBRO_PRECIO, binding.etPrecioLibro.text.toString().toDouble())
            put(ConnectionClass.COL_LIBRO_DISPONIBLE, if (binding.cbDisponible.isChecked) 1 else 0)
            put(ConnectionClass.COL_LIBRO_FECHA_PUBLICACION, binding.etFechaPublicacion.text.toString().trim())
        }

        val selection = "${ConnectionClass.COL_LIBRO_ID} = ?"
        val selectionArgs = arrayOf(lId.toString())

        val count = db.update(ConnectionClass.TABLE_LIBRO, values, selection, selectionArgs)
        if (count > 0) {
            Toast.makeText(this, "Libro actualizado exitosamente", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar libro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteData() {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Libro")
            .setMessage("¿Estás seguro de que deseas eliminar este libro?")
            .setPositiveButton("Sí") { _, _ ->
                val selection = "${ConnectionClass.COL_LIBRO_ID} = ?"
                val selectionArgs = arrayOf(lId.toString())

                val count = db.delete(ConnectionClass.TABLE_LIBRO, selection, selectionArgs)
                if (count > 0) {
                    Toast.makeText(this, "Libro eliminado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al eliminar libro", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
