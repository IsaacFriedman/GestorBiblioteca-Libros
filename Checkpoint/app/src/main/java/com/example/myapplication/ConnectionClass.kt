package com.example.myapplication

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ConnectionClass(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "biblioteca_db.db"
        private const val DATABASE_VERSION = 3

        // Constantes para la tabla Biblioteca
        const val TABLE_BIBLIOTECA = "biblioteca"
        const val COL_BIBLIOTECA_ID = "id"
        const val COL_BIBLIOTECA_NOMBRE = "nombre"
        const val COL_BIBLIOTECA_DIRECCION = "direccion"
        const val COL_BIBLIOTECA_PRESUPUESTO = "presupuesto"
        const val COL_BIBLIOTECA_INAUGURADA = "inaugurada"

        // Constantes para la tabla Libro
        const val TABLE_LIBRO = "libro"
        const val COL_LIBRO_ID = "id"
        const val COL_LIBRO_BIBLIOTECA_ID = "biblioteca_id"
        const val COL_LIBRO_TITULO = "titulo"
        const val COL_LIBRO_AUTOR = "autor"
        const val COL_LIBRO_PRECIO = "precio"
        const val COL_LIBRO_DISPONIBLE = "disponible"
        const val COL_LIBRO_FECHA_PUBLICACION = "fecha_publicacion"

         @Volatile
        private var instance: ConnectionClass? = null
        private var database: SQLiteDatabase? = null

        fun getInstance(context: Context): ConnectionClass {
            return instance ?: synchronized(this) {
                instance ?: ConnectionClass(context.applicationContext).also { instance = it }
            }
        }
        fun getConnection(context: Context): SQLiteDatabase {
            if (database?.isOpen != true) {
                database = getInstance(context).writableDatabase
            }
            return database!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("ConnectionClass", "Creating tables")
        createTables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d("ConnectionClass", "Upgrading database from version $oldVersion to $newVersion")
        
        // Primero eliminamos las tablas existentes
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LIBRO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BIBLIOTECA")
        
        // Creamos las nuevas tablas
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        // Crear tabla Biblioteca
        db.execSQL("""
            CREATE TABLE $TABLE_BIBLIOTECA (
                $COL_BIBLIOTECA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_BIBLIOTECA_NOMBRE TEXT NOT NULL,
                $COL_BIBLIOTECA_DIRECCION TEXT NOT NULL,
                $COL_BIBLIOTECA_PRESUPUESTO REAL NOT NULL,
                $COL_BIBLIOTECA_INAUGURADA TEXT NOT NULL
            )
        """.trimIndent())

        // Crear tabla Libro
        db.execSQL("""
            CREATE TABLE $TABLE_LIBRO (
                $COL_LIBRO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_LIBRO_BIBLIOTECA_ID INTEGER NOT NULL,
                $COL_LIBRO_TITULO TEXT NOT NULL,
                $COL_LIBRO_AUTOR TEXT NOT NULL,
                $COL_LIBRO_PRECIO REAL NOT NULL,
                $COL_LIBRO_DISPONIBLE INTEGER NOT NULL,
                $COL_LIBRO_FECHA_PUBLICACION TEXT NOT NULL,
                FOREIGN KEY($COL_LIBRO_BIBLIOTECA_ID) REFERENCES $TABLE_BIBLIOTECA($COL_BIBLIOTECA_ID)
                ON DELETE CASCADE
            )
        """.trimIndent())
    }
}