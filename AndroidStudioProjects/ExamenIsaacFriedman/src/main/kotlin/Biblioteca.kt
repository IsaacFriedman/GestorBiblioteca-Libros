import java.util.*
import java.io.Serializable

data class Biblioteca(
    val id: Int,
    var nombre: String,
    var direccion: String,
    var presupuesto: Double,
    var inaugurada: Date,
    val libros: MutableList<Libro> = mutableListOf()
) : Serializable
