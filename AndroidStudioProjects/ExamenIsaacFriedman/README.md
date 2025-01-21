# Sistema de Gestión de Bibliotecas y Libros

Este proyecto implementa un sistema de gestión para bibliotecas y libros en Kotlin. Permite realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) tanto para bibliotecas como para los libros que pertenecen a ellas. Los datos se almacenan en archivos y son persistentes entre ejecuciones del programa.

## Características principales
- **Gestión de Bibliotecas**:
    - Crear, listar, actualizar y eliminar bibliotecas.
    - Cada biblioteca incluye atributos como ID, nombre, dirección, presupuesto y fecha de inauguración.
- **Gestión de Libros**:
    - Crear, listar, actualizar y eliminar libros asociados a una biblioteca.
    - Cada libro incluye atributos como ID, título, autor, precio, disponibilidad y fecha de publicación.
- **Persistencia de Datos**:
    - Los datos de bibliotecas y libros se almacenan en los archivos `bibliotecas.txt` y `libros.txt` en la carpeta `resources`.
    - Los cambios realizados durante la ejecución se guardan automáticamente.
- **Manejo de errores**:
    - Validación y manejo de errores de entrada del usuario para evitar interrupciones en la ejecución del programa.

## Estructura del Proyecto
```
├── kotlin/
│   ├── Biblioteca.kt           # Clase Biblioteca
│   ├── Libro.kt                # Clase Libro
│   ├── GestorBiblioteca.kt     # Lógica de gestión de bibliotecas
│   ├── GestorLibro.kt          # Lógica de gestión de libros
│   ├── Main.kt                 # Punto de entrada del programa
├── resources/
│   ├── bibliotecas.txt         # Archivo de datos de bibliotecas
│   ├── libros.txt              # Archivo de datos de libros
```

## Requisitos
- [Kotlin](https://kotlinlang.org/) configurado en tu sistema.
- Un IDE compatible como IntelliJ IDEA.

## Ejecución
1. Clona este repositorio.
2. Asegúrate de que los archivos `bibliotecas.txt` y `libros.txt` están en la carpeta `resources`.
3. Ejecuta el archivo `Main.kt` desde tu IDE o terminal.

## Instrucciones de uso
### Menú principal
Al iniciar el programa, se mostrará un menú principal con las siguientes opciones:
```
1. Gestionar Bibliotecas
2. Gestionar Libros
0. Salir
```
### Gestión de Bibliotecas
1. Crear una biblioteca ingresando el ID, nombre, dirección, presupuesto y fecha de inauguración.
2. Listar todas las bibliotecas registradas.
3. Actualizar una biblioteca existente ingresando su ID y los nuevos datos.
4. Eliminar una biblioteca ingresando su ID.

### Gestión de Libros
1. Agregar un libro a una biblioteca existente ingresando el ID de la biblioteca y los datos del libro.
2. Listar todos los libros de una biblioteca específica.
3. Actualizar los datos de un libro específico ingresando el ID de la biblioteca y el ID del libro.
4. Eliminar un libro ingresando el ID de la biblioteca y el ID del libro.

## Persistencia de datos
- **Archivo `bibliotecas.txt`**:
    - Contiene las bibliotecas en el siguiente formato:
      ```
      ID|Nombre|Dirección|Presupuesto|FechaInauguración
      ```
- **Archivo `libros.txt`**:
    - Contiene los libros en el siguiente formato:
      ```
      ID|BibliotecaID|Título|Autor|Precio|Disponible|FechaPublicación
      ```

## Ejemplo de datos
### `bibliotecas.txt`
```
1|Biblioteca Central|Avenida Principal 123|50000.0|2020-05-15
2|Biblioteca Norte|Calle Secundaria 45|30000.0|2018-03-10
```
### `libros.txt`
```
1|1|El Quijote|Miguel de Cervantes|25.0|true|1605-01-16
2|1|1984|George Orwell|15.0|true|1949-06-08
3|2|Cien Años de Soledad|Gabriel García Márquez|20.0|true|1967-05-30
```

## Notas
- La persistencia de datos se asegura cada vez que se realiza una operación de creación, actualización o eliminación.
- Si algún archivo de datos (`bibliotecas.txt` o `libros.txt`) no existe, el programa lo creará automáticamente.
