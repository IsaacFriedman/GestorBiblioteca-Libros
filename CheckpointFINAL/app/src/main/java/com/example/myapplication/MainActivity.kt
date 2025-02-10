package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private var db: SQLiteDatabase? = null
    private var mMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createDatabase()
        setupSpinner()
        setupButtons()

        // Inicializar el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun createDatabase() {
        try {
            db = ConnectionClass.getConnection(this)
            binding.tvStatus.text = "Estado: Base de datos creada exitosamente"
        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvStatus.text = "Estado: Error - ${e.message}"
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSpinner() {
        val opciones = arrayOf("Seleccione una opción", "Biblioteca", "Libro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTipo.adapter = adapter

        binding.spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // Ninguna selección
                        deshabilitarBotones()
                    }
                    1 -> { // Biblioteca
                        habilitarBotones()
                    }
                    2 -> { // Libro
                        if (bibliotecasDisponibles()) {
                            habilitarBotones()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Debe registrar al menos una biblioteca primero",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.spinnerTipo.setSelection(0)
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                deshabilitarBotones()
            }
        }
    }

    private fun setupButtons() {
        binding.btnInsert.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Biblioteca
                    val intent = Intent(this, Biblioteca::class.java)
                    intent.putExtra("msg", "add")
                    startActivity(intent)
                }
                2 -> { // Libro
                    if (bibliotecasDisponibles()) {
                        val intent = Intent(this, Libro::class.java)
                        intent.putExtra("msg", "add")
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this,
                            "Debe registrar al menos una biblioteca primero",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.spinnerTipo.setSelection(0)
                    }
                }
            }
        }

        binding.btnEdit.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Biblioteca
                    val intent = Intent(this, ListaBibliotecasActivity::class.java)
                    intent.putExtra("action", "edit")
                    startActivity(intent)
                }
                2 -> { // Libro
                    val intent = Intent(this, ListaBibliotecasActivity::class.java)
                    intent.putExtra("action", "edit_libros")
                    startActivity(intent)
                }
            }
        }

        binding.btnDelete.setOnClickListener {
            when (binding.spinnerTipo.selectedItemPosition) {
                1 -> { // Biblioteca
                    val intent = Intent(this, ListaBibliotecasActivity::class.java)
                    intent.putExtra("action", "delete")
                    startActivity(intent)
                }
                2 -> { // Libro
                    val intent = Intent(this, ListaBibliotecasActivity::class.java)
                    intent.putExtra("action", "delete_libros")
                    startActivity(intent)
                }
            }
        }
    }

    private fun habilitarBotones() {
        binding.btnInsert.isEnabled = true
        binding.btnEdit.isEnabled = true
        binding.btnDelete.isEnabled = true
    }

    private fun deshabilitarBotones() {
        binding.btnInsert.isEnabled = false
        binding.btnEdit.isEnabled = false
        binding.btnDelete.isEnabled = false
    }

    private fun bibliotecasDisponibles(): Boolean {
        return try {
            db = ConnectionClass.getConnection(this)

            val cursor = db?.rawQuery("SELECT COUNT(*) FROM ${ConnectionClass.TABLE_BIBLIOTECA}", null)
            cursor?.use {
                if (it.moveToFirst()) {
                    return it.getInt(0) > 0
                }
                false
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error al verificar bibliotecas: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        actualizarMarcadoresBibliotecas()
    }

    private fun actualizarMarcadoresBibliotecas() {
        mMap?.clear() // Limpiar marcadores existentes
        
        try {
            val cursor = db?.rawQuery(
                """
                SELECT nombre, latitud, longitud
                FROM ${ConnectionClass.TABLE_BIBLIOTECA}
                """,
                null
            )

            cursor?.use {
                while (it.moveToNext()) {
                    val nombre = it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_NOMBRE))
                    val latitud = it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_LATITUD))
                    val longitud = it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_LONGITUD))
                    
                    val posicion = LatLng(latitud, longitud)
                    mMap?.addMarker(MarkerOptions().position(posicion).title(nombre))
                }

                // Centrar el mapa en Quito
                val quito = LatLng(-0.225219, -78.5248)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(quito, 12f))
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar bibliotecas en el mapa: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        db = ConnectionClass.getConnection(this)
        actualizarMarcadoresBibliotecas()
        
        if (binding.spinnerTipo.selectedItemPosition == 2 && !bibliotecasDisponibles()) {
            binding.spinnerTipo.setSelection(0)
        }
    }
}