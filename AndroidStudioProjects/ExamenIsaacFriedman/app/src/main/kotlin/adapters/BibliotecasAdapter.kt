class BibliotecasAdapter(
    private var bibliotecas: List<Biblioteca>,
    private val onBibliotecaClick: (Biblioteca) -> Unit
) : RecyclerView.Adapter<BibliotecasAdapter.BibliotecaViewHolder>() {

    class BibliotecaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreTextView: TextView = view.findViewById(R.id.nombreBiblioteca)
        val direccionTextView: TextView = view.findViewById(R.id.direccionBiblioteca)
        val btnEditar: ImageButton = view.findViewById(R.id.btnEditar)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BibliotecaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_biblioteca, parent, false)
        return BibliotecaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BibliotecaViewHolder, position: Int) {
        val biblioteca = bibliotecas[position]
        holder.nombreTextView.text = biblioteca.nombre
        holder.direccionTextView.text = biblioteca.direccion
        
        holder.itemView.setOnClickListener { onBibliotecaClick(biblioteca) }
    }

    override fun getItemCount() = bibliotecas.size

    fun actualizarBibliotecas(nuevasBibliotecas: List<Biblioteca>) {
        bibliotecas = nuevasBibliotecas
        notifyDataSetChanged()
    }
} 