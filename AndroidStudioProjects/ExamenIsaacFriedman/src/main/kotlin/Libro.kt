import java.util.*
import java.io.Serializable

data class Libro(
    val id: Int,
    var titulo: String,
    var autor: String,
    var precio: Double,
    var disponible: Boolean,
    var fechaPublicacion: Date
) : Serializable
