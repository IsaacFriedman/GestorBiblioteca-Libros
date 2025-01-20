package com.example.myapplication

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityListaLibrosBinding

class ListaLibrosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListaLibrosBinding
    private var db: SQLiteDatabase? = null
    private var bId: Int = 0
    private var action: String = "edit_libros"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaLibrosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bId = intent.getIntExtra("bId", 0)
        action = intent.getStringExtra("action") ?: "edit_libros"

        db = ConnectionClass.getConnection(this)
        cargarLibros()
    }

    private fun cargarLibros() {
        try {
            val cursor = db?.rawQuery(
                """
                SELECT 
                    ${ConnectionClass.COL_LIBRO_ID},
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
                libros
            ) { libro ->
                val intent = Intent(this, Libro::class.java).apply {
                    putExtra("msg", action)
                    putExtra("lId", libro["id"] as Int)
                    putExtra("bId", bId)
                }
                startActivity(intent)
                if (action == "delete_libros") {
                    finish()
                }
            }

            binding.listViewLibros.adapter = adapter

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar libros: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarLibros()
    }
}