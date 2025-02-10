package com.example.myapplication

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityBibliotecaBinding
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.ArrayAdapter

class Biblioteca : AppCompatActivity() {
    private lateinit var binding: ActivityBibliotecaBinding
    private var db: SQLiteDatabase? = null
    private var msg = ""
    private var bId = 0

    private data class ZonaUbicacion(
        val nombre: String,
        val latitud: Double,
        val longitud: Double
    )

    private val zonas = listOf(
        ZonaUbicacion("Norte", -0.175, -78.495),
        ZonaUbicacion("Centro", -0.230, -78.525),
        ZonaUbicacion("Sur", -0.280, -78.540)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = ConnectionClass.getConnection(this)
        msg = intent.getStringExtra("msg").toString()
        bId = intent.getIntExtra("bId", 0)

        when (msg) {
            "add" -> {
                binding.btnSave.text = "Guardar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
            }
            "edit" -> {
                binding.btnSave.text = "Actualizar"
                binding.btnSave.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.GONE
                habilitarCampos(true)
                cargarDatosBiblioteca()
            }
            "delete" -> {
                binding.btnSave.visibility = View.GONE
                binding.btnDelete.visibility = View.VISIBLE
                habilitarCampos(false)
                cargarDatosBiblioteca()
            }
        }

        binding.btnSave.setOnClickListener {
            when (msg) {
                "add" -> insertData()
                "edit" -> updateData()
            }
        }

        binding.btnDelete.setOnClickListener {
            confirmarEliminacion()
        }

        setupZonaSpinner()
    }

    private fun habilitarCampos(enabled: Boolean) {
        binding.etNombre.isEnabled = enabled
        binding.etDireccion.isEnabled = enabled
        binding.etPresupuesto.isEnabled = enabled
        binding.etInaugurada.isEnabled = enabled
        binding.spinnerZona.isEnabled = enabled
    }

    private fun cargarDatosBiblioteca() {
        binding.etNombre.setText(intent.getStringExtra("nombre"))
        binding.etDireccion.setText(intent.getStringExtra("direccion"))
        binding.etPresupuesto.setText(intent.getDoubleExtra("presupuesto", 0.0).toString())
        binding.etInaugurada.setText(intent.getStringExtra("inaugurada"))
        
        val zona = intent.getStringExtra("zona")
        val position = zonas.indexOfFirst { it.nombre == zona }
        if (position != -1) {
            binding.spinnerZona.setSelection(position)
        }
    }

    private fun validarCampos(): Boolean {
        if (binding.etNombre.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El nombre es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etDireccion.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La dirección es requerida", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etPresupuesto.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "El presupuesto es requerido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etInaugurada.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "La fecha de inauguración es requerida", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(binding.etInaugurada.text.toString().trim())
        } catch (e: Exception) {
            Toast.makeText(this, "Formato de fecha inválido. Use yyyy/mm/dd", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun setupZonaSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            zonas.map { it.nombre }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerZona.adapter = adapter

        // Si estamos en modo edición o eliminación, seleccionar la zona correcta
        if (msg == "edit" || msg == "delete") {
            val zonaActual = intent.getStringExtra("zona")
            val position = zonas.indexOfFirst { it.nombre == zonaActual }
            if (position != -1) {
                binding.spinnerZona.setSelection(position)
            }
        }
    }

    private fun insertData() {
        if (!validarCampos()) return

        try {
            val zonaSeleccionada = zonas[binding.spinnerZona.selectedItemPosition]
            
            val values = android.content.ContentValues().apply {
                put(ConnectionClass.COL_BIBLIOTECA_NOMBRE, binding.etNombre.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_DIRECCION, binding.etDireccion.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_PRESUPUESTO, binding.etPresupuesto.text.toString().toDouble())
                put(ConnectionClass.COL_BIBLIOTECA_INAUGURADA, binding.etInaugurada.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_LATITUD, zonaSeleccionada.latitud)
                put(ConnectionClass.COL_BIBLIOTECA_LONGITUD, zonaSeleccionada.longitud)
                put(ConnectionClass.COL_BIBLIOTECA_ZONA, zonaSeleccionada.nombre)
            }

            val result = db?.insert(ConnectionClass.TABLE_BIBLIOTECA, null, values)
            if (result != -1L) {
                Toast.makeText(this, "Biblioteca registrada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al registrar biblioteca", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateData() {
        if (!validarCampos()) return

        try {
            val zonaSeleccionada = zonas[binding.spinnerZona.selectedItemPosition]
            
            val values = android.content.ContentValues().apply {
                put(ConnectionClass.COL_BIBLIOTECA_NOMBRE, binding.etNombre.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_DIRECCION, binding.etDireccion.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_PRESUPUESTO, binding.etPresupuesto.text.toString().toDouble())
                put(ConnectionClass.COL_BIBLIOTECA_INAUGURADA, binding.etInaugurada.text.toString().trim())
                put(ConnectionClass.COL_BIBLIOTECA_LATITUD, zonaSeleccionada.latitud)
                put(ConnectionClass.COL_BIBLIOTECA_LONGITUD, zonaSeleccionada.longitud)
                put(ConnectionClass.COL_BIBLIOTECA_ZONA, zonaSeleccionada.nombre)
            }

            val selection = "${ConnectionClass.COL_BIBLIOTECA_ID} = ?"
            val selectionArgs = arrayOf(bId.toString())

            val count = db?.update(ConnectionClass.TABLE_BIBLIOTECA, values, selection, selectionArgs)
            if (count != null && count > 0) {
                Toast.makeText(this, "Biblioteca actualizada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al actualizar biblioteca", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmarEliminacion() {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Está seguro que desea eliminar esta biblioteca?")
            .setPositiveButton("Sí") { _, _ ->
                deleteData()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteData() {
        try {
            val selection = "${ConnectionClass.COL_BIBLIOTECA_ID} = ?"
            val selectionArgs = arrayOf(bId.toString())

            val count = db?.delete(ConnectionClass.TABLE_BIBLIOTECA, selection, selectionArgs)
            if (count != null && count > 0) {
                Toast.makeText(this, "Biblioteca eliminada exitosamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Error al eliminar biblioteca", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db?.close()
    }
}