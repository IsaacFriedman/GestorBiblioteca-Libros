import java.io.File
import java.text.SimpleDateFormat


class GestorBiblioteca {
    private val bibliotecas = mutableListOf<Biblioteca>()

    // Agregar una nueva biblioteca
    fun agregarBiblioteca(biblioteca: Biblioteca) {
        try {
            bibliotecas.add(biblioteca)
            println("Biblioteca agregada exitosamente: $biblioteca")
            guardarBibliotecasEnArchivo("bibliotecas.txt")
        } catch (e: Exception) {
            println("Error al agregar la biblioteca: ${e.message}")
        }
    }


    // Listar todas las bibliotecas
    fun listarBibliotecas(): List<Biblioteca> {
        if (bibliotecas.isEmpty()) {
            println("No hay bibliotecas registradas.")
        }
        return bibliotecas
    }

    // Actualizar una biblioteca existente
    fun actualizarBiblioteca(id: Int, nuevaBiblioteca: Biblioteca) {
        val index = bibliotecas.indexOfFirst { it.id == id }
        if (index != -1) {
            bibliotecas[index] = nuevaBiblioteca
            println("Biblioteca actualizada exitosamente: $nuevaBiblioteca")
        } else {
            println("Biblioteca con ID $id no encontrada.")
        }
    }

    // Eliminar una biblioteca por ID
    fun eliminarBiblioteca(id: Int) {
        val eliminada = bibliotecas.removeIf { it.id == id }
        if (eliminada) {
            println("Biblioteca con ID $id eliminada exitosamente.")
        } else {
            println("Biblioteca con ID $id no encontrada.")
        }
    }

    // Buscar una biblioteca por ID
    fun obtenerBibliotecaPorId(id: Int): Biblioteca? {
        return bibliotecas.find { it.id == id } ?: run {
            println("Biblioteca con ID $id no encontrada.")
            null
        }
    }
    fun cargarBibliotecasDesdeArchivo(rutaArchivo: String) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val archivo = File(javaClass.classLoader.getResource(rutaArchivo)?.file ?: "")

        if (archivo.exists()) {
            archivo.forEachLine { linea ->
                val partes = linea.split("|")
                if (partes.size == 5) {
                    val id = partes[0].toInt()
                    val nombre = partes[1]
                    val direccion = partes[2]
                    val presupuesto = partes[3].toDouble()
                    val inaugurada = dateFormatter.parse(partes[4])
                    agregarBiblioteca(Biblioteca(id, nombre, direccion, presupuesto, inaugurada))
                }
            }
            println("Bibliotecas cargadas exitosamente desde $rutaArchivo.")
        } else {
            println("No se encontró el archivo $rutaArchivo.")
        }
    }

    fun guardarBibliotecasEnArchivo(rutaArchivo: String) {
        try {
            val archivo = File(javaClass.classLoader.getResource(rutaArchivo)?.file ?: "")
            archivo.printWriter().use { out ->
                bibliotecas.forEach { biblioteca ->
                    val line = "${biblioteca.id}|${biblioteca.nombre}|${biblioteca.direccion}|${biblioteca.presupuesto}|${SimpleDateFormat("yyyy-MM-dd").format(biblioteca.inaugurada)}"
                    out.println(line)
                }
            }
            println("Bibliotecas guardadas exitosamente en $rutaArchivo.")
        } catch (e: Exception) {
            println("Error al guardar bibliotecas en archivo: ${e.message}")
        }
    }

}
