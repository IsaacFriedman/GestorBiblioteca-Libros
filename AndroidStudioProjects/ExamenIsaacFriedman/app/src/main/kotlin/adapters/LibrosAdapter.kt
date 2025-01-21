import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LibrosAdapter(
    private var libros: List<Libro>,
    private val onLibroClick: (Libro) -> Unit
) : RecyclerView.Adapter<LibrosAdapter.LibroViewHolder>() {

    class LibroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tituloTextView: TextView = view.findViewById(R.id.tituloLibro)
        val autorTextView: TextView = view.findViewById(R.id.autorLibro)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_libro, parent, false)
        return LibroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        val libro = libros[position]
        holder.tituloTextView.text = libro.titulo
        holder.autorTextView.text = libro.autor
        
        holder.itemView.setOnClickListener { onLibroClick(libro) }
    }

    override fun getItemCount() = libros.size

    fun actualizarLibros(nuevosLibros: List<Libro>) {
        libros = nuevosLibros
        notifyDataSetChanged()
    }
} 