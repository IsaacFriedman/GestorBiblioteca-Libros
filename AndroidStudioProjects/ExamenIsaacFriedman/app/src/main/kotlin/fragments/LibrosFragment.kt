import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LibrosFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibrosAdapter
    private lateinit var gestorLibro: GestorLibro

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_libros, container, false)
        
        recyclerView = view.findViewById(R.id.librosRecyclerView)
        adapter = LibrosAdapter(emptyList()) { libro ->
            mostrarDetallesLibro(libro)
        }
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        view.findViewById<FloatingActionButton>(R.id.fabAgregarLibro).setOnClickListener {
            mostrarDialogoCrearLibro()
        }

        cargarLibros()
        
        return view
    }

    private fun cargarLibros() {
        // Implementar la carga de libros
    }

    private fun mostrarDetallesLibro(libro: Libro) {
        // Por ahora solo mostraremos un Toast
        Toast.makeText(context, "Libro: ${libro.titulo}", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoCrearLibro() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_libro, null)
        
        builder.setView(dialogView)
            .setTitle("Crear Libro")
            .setPositiveButton("Guardar") { dialog, _ ->
                // Implementar la lógica para guardar
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.cancel()
            }
        
        builder.create().show()
    }
} 