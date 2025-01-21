package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaLibrosBinding

class ListaLibrosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaLibrosBinding
    private var db: SQLiteDatabase? = null
    private var bId: Int = 0
    private var action: String = "edit"
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaLibrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bId = intent.getIntExtra("bId", 0)
        action = intent.getStringExtra("action") ?: "edit"
        isEditMode = action == "edit"

        binding.tvTitulo.text = if (action == "delete") "Eliminar Libros" else "Lista de Libros"

        db = ConnectionClass.getConnection(this)
        cargarLibros()
    }

    private fun cargarLibros() {
        try {
            val cursor = db?.rawQuery(
                """
                SELECT 
                    ${ConnectionClass.COL_LIBRO_ID},
                    ${ConnectionClass.COL_LIBRO_BIBLIOTECA_ID},
                    ${ConnectionClass.COL_LIBRO_TITULO},
                    ${ConnectionClass.COL_LIBRO_AUTOR},
                    ${ConnectionClass.COL_LIBRO_PRECIO},
                    ${ConnectionClass.COL_LIBRO_DISPONIBLE},
                    ${ConnectionClass.COL_LIBRO_FECHA_PUBLICACION}
                FROM ${ConnectionClass.TABLE_LIBRO}
                WHERE ${ConnectionClass.COL_LIBRO_BIBLIOTECA_ID} = ?
                """,
                arrayOf(bId.toString())
            )

            val libros = mutableListOf<Map<String, Any>>()

            cursor?.use {
                while (it.moveToNext()) {
                    libros.add(
                        mapOf(
                            "id" to it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_ID)),
                            "biblioteca_id" to it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_BIBLIOTECA_ID)),
                            "titulo" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_TITULO)),
                            "autor" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_AUTOR)),
                            "precio" to it.getDouble(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_PRECIO)),
                            "disponible" to it.getInt(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_DISPONIBLE)),
                            "fecha_publicacion" to it.getString(it.getColumnIndexOrThrow(ConnectionClass.COL_LIBRO_FECHA_PUBLICACION))
                        )
                    )
                }
            }

            if (libros.isEmpty()) {
                Toast.makeText(this, "No hay libros registrados", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val adapter = LibroAdapter(
                this,
                libros,
                showDeleteButton = (action == "delete"),
                onItemClick = { libro ->
                    if (action == "edit") {
                        isEditMode = true
                        val intent = Intent(this, Libro::class.java).apply {
                            putExtra("msg", "edit")
                            putExtra("lId", libro["id"] as Int)
                            putExtra("bId", libro["biblioteca_id"] as Int)
                            putExtra("titulo", libro["titulo"] as String)
                            putExtra("autor", libro["autor"] as String)
                            putExtra("precio", libro["precio"] as Double)
                            putExtra("disponible", libro["disponible"] as Int)
                            putExtra("fecha_publicacion", libro["fecha_publicacion"] as String)
                        }
                        startActivity(intent)
                        cargarLibros()
                    }
                },
                onDeleteClick = { libro ->
                    AlertDialog.Builder(this)
                        .setTitle("Eliminar Libro")
                        .setMessage("¿Está seguro que desea eliminar este libro?")
                        .setPositiveButton("Sí") { _, _ ->
                            val lId = libro["id"] as Int
                            val count = db?.delete(
                                ConnectionClass.TABLE_LIBRO,
                                "${ConnectionClass.COL_LIBRO_ID} = ?",
                                arrayOf(lId.toString())
                            ) ?: 0

                            if (count > 0) {
                                Toast.makeText(this, "Libro eliminado exitosamente", Toast.LENGTH_SHORT).show()
                                cargarLibros()
                            } else {
                                Toast.makeText(this, "Error al eliminar libro", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .setNegativeButton("No", null)
                        .show()
                }
            )

            binding.listViewLibros.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        isEditMode = action == "edit"
        cargarLibros()
    }
}
