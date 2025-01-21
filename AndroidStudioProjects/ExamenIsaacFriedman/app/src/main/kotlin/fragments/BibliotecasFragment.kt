class BibliotecasFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BibliotecasAdapter
    private lateinit var gestorBiblioteca: GestorBiblioteca

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bibliotecas, container, false)
        
        recyclerView = view.findViewById(R.id.bibliotecasRecyclerView)
        adapter = BibliotecasAdapter(emptyList()) { biblioteca ->
            // Acción al hacer clic en una biblioteca
            mostrarDetallesBiblioteca(biblioteca)
        }
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Botón flotante para agregar biblioteca
        view.findViewById<FloatingActionButton>(R.id.fabAgregarBiblioteca).setOnClickListener {
            mostrarDialogoCrearBiblioteca()
        }

        cargarBibliotecas()
        
        return view
    }

    private fun cargarBibliotecas() {
        // Cargar bibliotecas usando tu GestorBiblioteca
        val bibliotecas = gestorBiblioteca.listarBibliotecas()
        adapter.actualizarBibliotecas(bibliotecas)
    }

    private fun mostrarDetallesBiblioteca(biblioteca: Biblioteca) {
        // Por ahora solo mostraremos un Toast
        Toast.makeText(context, "Biblioteca: ${biblioteca.nombre}", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoCrearBiblioteca() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_biblioteca, null)
        
        builder.setView(dialogView)
            .setTitle("Crear Biblioteca")
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