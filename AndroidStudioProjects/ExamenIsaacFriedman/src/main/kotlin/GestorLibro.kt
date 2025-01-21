import java.io.File
import java.text.SimpleDateFormat

class GestorLibro(private val gestorBiblioteca: GestorBiblioteca) {

    // Agregar un libro a una biblioteca
    fun agregarLibro(bibliotecaId: Int, libro: Libro) {
        try {
            val biblioteca = gestorBiblioteca.listarBibliotecas().find { it.id == bibliotecaId }
            if (biblioteca != null) {
                biblioteca.libros.add(libro)
                println("Libro agregado exitosamente a la biblioteca ${biblioteca.nombre}: $libro")
                guardarLibrosEnArchivo("libros.txt")
            } else {
                println("No se pudo agregar el libro. Biblioteca con ID $bibliotecaId no encontrada.")
            }
        } catch (e: Exception) {
            println("Error al agregar el libro: ${e.message}")
        }
    }

    // Listar los libros de una biblioteca
    fun listarLibros(bibliotecaId: Int): List<Libro>? {
        val biblioteca = gestorBiblioteca.obtenerBibliotecaPorId(bibliotecaId)
        return if (biblioteca != null) {
            if (biblioteca.libros.isEmpty()) {
                println("No hay libros registrados en la biblioteca ${biblioteca.nombre}.")
            }
            biblioteca.libros
        } else {
            println("Biblioteca con ID $bibliotecaId no encontrada.")
            null
        }
    }

    // Actualizar un libro en una biblioteca
    fun actualizarLibro(bibliotecaId: Int, libroId: Int, nuevosDatos: Libro) {
        val biblioteca = gestorBiblioteca.obtenerBibliotecaPorId(bibliotecaId)
        if (biblioteca != null) {
            val index = biblioteca.libros.indexOfFirst { it.id == libroId }
            if (index != -1) {
                biblioteca.libros[index] = nuevosDatos
                println("Libro actualizado exitosamente en la biblioteca ${biblioteca.nombre}: $nuevosDatos")
            } else {
                println("Libro con ID $libroId no encontrado en la biblioteca ${biblioteca.nombre}.")
            }
        } else {
            println("Biblioteca con ID $bibliotecaId no encontrada.")
        }
    }

    // Eliminar un libro de una biblioteca
    fun eliminarLibro(bibliotecaId: Int, libroId: Int) {
        val biblioteca = gestorBiblioteca.obtenerBibliotecaPorId(bibliotecaId)
        if (biblioteca != null) {
            val eliminado = biblioteca.libros.removeIf { it.id == libroId }
            if (eliminado) {
                println("Libro con ID $libroId eliminado exitosamente de la biblioteca ${biblioteca.nombre}.")
            } else {
                println("Libro con ID $libroId no encontrado en la biblioteca ${biblioteca.nombre}.")
            }
        } else {
            println("Biblioteca con ID $bibliotecaId no encontrada.")
        }
    }

    fun cargarLibrosDesdeArchivo(rutaArchivo: String) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        val archivo = File(javaClass.classLoader.getResource(rutaArchivo)?.file ?: "")

        if (archivo.exists()) {
            archivo.forEachLine { linea ->
                val partes = linea.split("|")
                if (partes.size == 7) {
                    val libroId = partes[0].toInt()
                    val bibliotecaId = partes[1].toInt()
                    val titulo = partes[2]
                    val autor = partes[3]
                    val precio = partes[4].toDouble()
                    val disponible = partes[5].toBoolean()
                    val fechaPublicacion = dateFormatter.parse(partes[6])

                    val libro = Libro(libroId, titulo, autor, precio, disponible, fechaPublicacion)
                    agregarLibro(bibliotecaId, libro)
                }
            }
            println("Libros cargados exitosamente desde $rutaArchivo.")
        } else {
            println("No se encontró el archivo $rutaArchivo.")
        }
    }

    fun guardarLibrosEnArchivo(rutaArchivo: String) {
        try {
            val archivo = File(javaClass.classLoader.getResource(rutaArchivo)?.file ?: "")
            archivo.printWriter().use { out ->
                gestorBiblioteca.listarBibliotecas().forEach { biblioteca ->
                    biblioteca.libros.forEach { libro ->
                        val line = "${libro.id}|${biblioteca.id}|${libro.titulo}|${libro.autor}|${libro.precio}|${libro.disponible}|${SimpleDateFormat("yyyy-MM-dd").format(libro.fechaPublicacion)}"
                        out.println(line)
                    }
                }
            }
            println("Libros guardados exitosamente en $rutaArchivo.")
        } catch (e: Exception) {
            println("Error al guardar libros en archivo: ${e.message}")
        }
    }

}
