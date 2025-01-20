package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaBibliotecasBinding

class ListaBibliotecasActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaBibliotecasBinding
    private var db: SQLiteDatabase? = null
    private var action: String = "edit"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaBibliotecasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        action = intent.getStringExtra("action") ?: "edit"

        binding.tvTitulo.text = when (action) {
            "edit" -> "Editar Biblioteca"
            "delete" -> "Eliminar Biblioteca"
            "edit_libros" -> "Elegir Biblioteca"
            else -> "Lista de Bibliotecas"
        }

        db = ConnectionClass.getConnection(this)
        cargarBibliotecas()
    }

    private fun cargarBibliotecas() {
        try {
            val cursor = db?.rawQuery(
                """
                SELECT id, nombre, direccion, presupuesto, inaugurada
                FROM ${ConnectionClass.TABLE_BIBLIOTECA}
                """, null
            )

            val bibliotecas = mutableListOf<Map<String, Any>>()

            cursor?.use {
                while (it.moveToNext()) {
                    bibliotecas.add(
                        mapOf(
                            "id" to it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_ID)),
                            "nombre" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_NOMBRE)),
                            "direccion" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_DIRECCION)),
                            "presupuesto" to it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_PRESUPUESTO)),
                            "inaugurada" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_BIBLIOTECA_INAUGURADA))
                        )
                    )
                }
            }

            if (bibliotecas.isEmpty()) {
                Toast.makeText(this, "No hay bibliotecas registradas", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val adapter = BibliotecaAdapter(
                this,
                bibliotecas,
                showDeleteButton = action == "delete",
                onItemClick = { biblioteca ->
                    when (action) {
                        "edit" -> {
                            val intent = Intent(this, Biblioteca::class.java).apply {
                                putExtra("msg", "edit")
                                putExtra("bId", biblioteca["id"] as Int)
                                putExtra("nombre", biblioteca["nombre"] as String)
                                putExtra("direccion", biblioteca["direccion"] as String)
                                putExtra("presupuesto", biblioteca["presupuesto"] as Double)
                                putExtra("inaugurada", biblioteca["inaugurada"] as String)
                            }
                            startActivity(intent)
                            finish()
                        }
                        "edit_libros" -> {
                            val bId = biblioteca["id"] as Int
                            val cursor = db?.rawQuery(
                                "SELECT COUNT(*) FROM ${ConnectionClass.TABLE_LIBRO} WHERE ${ConnectionClass.COL_LIBRO_BIBLIOTECA_ID} = ?",
                                arrayOf(bId.toString())
                            )
                            cursor?.use {
                                if (it.moveToFirst() && it.getInt(0) > 0) {
                                    val intent = Intent(this, ListaLibrosActivity::class.java).apply {
                                        putExtra("bId", bId)
                                    }
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this,
                                        "No hay libros registrados para esta biblioteca",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        "delete_libros" -> {
                            val bId = biblioteca["id"] as Int
                            val cursor = db?.rawQuery(
                                "SELECT COUNT(*) FROM ${ConnectionClass.TABLE_LIBRO} WHERE ${ConnectionClass.COL_LIBRO_BIBLIOTECA_ID} = ?",
                                arrayOf(bId.toString())
                            )
                            cursor?.use {
                                if (it.moveToFirst() && it.getInt(0) > 0) {
                                    val intent = Intent(this, ListaLibrosActivity::class.java).apply {
                                        putExtra("bId", bId)
                                        putExtra("action", "delete_libros")
                                    }
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this,
                                        "No hay libros registrados para esta biblioteca",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                },
                onDeleteClick = { biblioteca ->
                    val intent = Intent(this, Biblioteca::class.java).apply {
                        putExtra("msg", "delete")
                        putExtra("bId", biblioteca["id"] as Int)
                        putExtra("nombre", biblioteca["nombre"] as String)
                        putExtra("direccion", biblioteca["direccion"] as String)
                        putExtra("presupuesto", biblioteca["presupuesto"] as Double)
                        putExtra("inaugurada", biblioteca["inaugurada"] as String)
                    }
                    startActivity(intent)
                    finish()
                }
            )

            binding.listViewBibliotecas.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar bibliotecas: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}