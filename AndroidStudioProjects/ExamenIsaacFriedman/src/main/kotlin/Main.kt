import java.text.SimpleDateFormat
import java.util.*
import java.io.File

fun main() {
    val gestorBiblioteca = GestorBiblioteca()
    val gestorLibro = GestorLibro(gestorBiblioteca)
    val scanner = Scanner(System.`in`)
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

    // Cargar datos desde archivos
    try {
        gestorBiblioteca.cargarBibliotecasDesdeArchivo("bibliotecas.txt")
    } catch (e: Exception) {
        println("Error al cargar bibliotecas: ${e.message}")
    }

    try {
        gestorLibro.cargarLibrosDesdeArchivo("libros.txt")
    } catch (e: Exception) {
        println("Error al cargar libros: ${e.message}")
    }

    while (true) {
        println("\n--- Menú Principal ---")
        println("1. Gestionar Bibliotecas")
        println("2. Gestionar Libros")
        println("0. Salir")
        print("Elige una opción: ")

        try {
            when (scanner.nextInt()) {
                1 -> gestionarBibliotecas(gestorBiblioteca, scanner, dateFormatter)
                2 -> gestionarLibros(gestorLibro, scanner, dateFormatter)
                0 -> {
                    println("Saliendo del programa...")
                    return
                }
                else -> println("Opción no válida.")
            }
        } catch (e: InputMismatchException) {
            println("Error: Entrada no válida. Por favor, intenta de nuevo.")
            scanner.nextLine() // Limpiar el buffer del scanner
        } catch (e: Exception) {
            println("Error inesperado: ${e.message}")
        }
    }
}

fun gestionarBibliotecas(gestorBiblioteca: GestorBiblioteca, scanner: Scanner, dateFormatter: SimpleDateFormat) {
    while (true) {
        println("\n--- Gestión de Bibliotecas ---")
        println("1. Crear Biblioteca")
        println("2. Listar Bibliotecas")
        println("3. Actualizar Biblioteca")
        println("4. Eliminar Biblioteca")
        println("0. Volver al menú principal")
        print("Elige una opción: ")

        try {
            when (scanner.nextInt()) {
                1 -> {
                    try {
                        print("Ingrese ID: ")
                        val id = scanner.nextInt()
                        scanner.nextLine()
                        print("Ingrese nombre: ")
                        val nombre = scanner.nextLine()
                        print("Ingrese dirección: ")
                        val direccion = scanner.nextLine()
                        print("Ingrese presupuesto: ")
                        val presupuesto = scanner.nextDouble()
                        scanner.nextLine()
                        print("Ingrese fecha de inauguración (yyyy-MM-dd): ")
                        val inaugurada = dateFormatter.parse(scanner.nextLine())
                        gestorBiblioteca.agregarBiblioteca(Biblioteca(id, nombre, direccion, presupuesto, inaugurada))
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar los datos correctamente.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al crear la biblioteca: ${e.message}")
                    }
                }
                2 -> {
                    try {
                        gestorBiblioteca.listarBibliotecas().forEach { println(it) }
                    } catch (e: Exception) {
                        println("Error al listar bibliotecas: ${e.message}")
                    }
                }
                3 -> {
                    try {
                        print("Ingrese ID de la biblioteca a actualizar: ")
                        val id = scanner.nextInt()
                        scanner.nextLine()
                        print("Ingrese nuevo nombre: ")
                        val nombre = scanner.nextLine()
                        print("Ingrese nueva dirección: ")
                        val direccion = scanner.nextLine()
                        print("Ingrese nuevo presupuesto: ")
                        val presupuesto = scanner.nextDouble()
                        scanner.nextLine()
                        print("Ingrese nueva fecha de inauguración (yyyy-MM-dd): ")
                        val inaugurada = dateFormatter.parse(scanner.nextLine())
                        gestorBiblioteca.actualizarBiblioteca(id, Biblioteca(id, nombre, direccion, presupuesto, inaugurada))
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar los datos correctamente.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al actualizar la biblioteca: ${e.message}")
                    }
                }
                4 -> {
                    try {
                        print("Ingrese ID de la biblioteca a eliminar: ")
                        val id = scanner.nextInt()
                        gestorBiblioteca.eliminarBiblioteca(id)
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar un número.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al eliminar la biblioteca: ${e.message}")
                    }
                }
                0 -> return
                else -> println("Opción no válida.")
            }
        } catch (e: InputMismatchException) {
            println("Error: Entrada no válida. Por favor, intenta de nuevo.")
            scanner.nextLine()
        } catch (e: Exception) {
            println("Error inesperado: ${e.message}")
        }
    }
}

fun gestionarLibros(gestorLibro: GestorLibro, scanner: Scanner, dateFormatter: SimpleDateFormat) {
    while (true) {
        println("\n--- Gestión de Libros ---")
        println("1. Agregar Libro")
        println("2. Listar Libros de una Biblioteca")
        println("3. Actualizar Libro")
        println("4. Eliminar Libro")
        println("0. Volver al menú principal")
        print("Elige una opción: ")

        try {
            when (scanner.nextInt()) {
                1 -> {
                    try {
                        print("Ingrese ID de la biblioteca: ")
                        val bibliotecaId = scanner.nextInt()
                        scanner.nextLine()
                        print("Ingrese ID del libro: ")
                        val id = scanner.nextInt()
                        scanner.nextLine()
                        print("Ingrese título: ")
                        val titulo = scanner.nextLine()
                        print("Ingrese autor: ")
                        val autor = scanner.nextLine()
                        print("Ingrese precio: ")
                        val precio = scanner.nextDouble()
                        scanner.nextLine()
                        print("¿Está disponible? (true/false): ")
                        val disponible = scanner.nextBoolean()
                        scanner.nextLine()
                        print("Ingrese fecha de publicación (yyyy-MM-dd): ")
                        val fechaPublicacion = dateFormatter.parse(scanner.nextLine())
                        gestorLibro.agregarLibro(bibliotecaId, Libro(id, titulo, autor, precio, disponible, fechaPublicacion))
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar los datos correctamente.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al agregar el libro: ${e.message}")
                    }
                }
                2 -> {
                    try {
                        print("Ingrese ID de la biblioteca: ")
                        val bibliotecaId = scanner.nextInt()
                        val libros = gestorLibro.listarLibros(bibliotecaId)
                        if (libros != null) libros.forEach { println(it) }
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar un número.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al listar los libros: ${e.message}")
                    }
                }
                3 -> {
                    try {
                        print("Ingrese ID de la biblioteca: ")
                        val bibliotecaId = scanner.nextInt()
                        print("Ingrese ID del libro: ")
                        val libroId = scanner.nextInt()
                        scanner.nextLine()
                        print("Ingrese nuevo título: ")
                        val titulo = scanner.nextLine()
                        print("Ingrese nuevo autor: ")
                        val autor = scanner.nextLine()
                        print("Ingrese nuevo precio: ")
                        val precio = scanner.nextDouble()
                        scanner.nextLine()
                        print("¿Está disponible? (true/false): ")
                        val disponible = scanner.nextBoolean()
                        scanner.nextLine()
                        print("Ingrese nueva fecha de publicación (yyyy-MM-dd): ")
                        val fechaPublicacion = dateFormatter.parse(scanner.nextLine())
                        gestorLibro.actualizarLibro(bibliotecaId, libroId, Libro(libroId, titulo, autor, precio, disponible, fechaPublicacion))
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar los datos correctamente.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al actualizar el libro: ${e.message}")
                    }
                }
                4 -> {
                    try {
                        print("Ingrese ID de la biblioteca: ")
                        val bibliotecaId = scanner.nextInt()
                        print("Ingrese ID del libro: ")
                        val libroId = scanner.nextInt()
                        gestorLibro.eliminarLibro(bibliotecaId, libroId)
                    } catch (e: InputMismatchException) {
                        println("Error: Entrada no válida. Asegúrate de ingresar un número.")
                        scanner.nextLine()
                    } catch (e: Exception) {
                        println("Error al eliminar el libro: ${e.message}")
                    }
                }
                0 -> return
                else -> println("Opción no válida.")
            }
        } catch (e: InputMismatchException) {
            println("Error: Entrada no válida. Por favor, intenta de nuevo.")
            scanner.nextLine()
        } catch (e: Exception) {
            println("Error inesperado: ${e.message}")
        }
    }
}
