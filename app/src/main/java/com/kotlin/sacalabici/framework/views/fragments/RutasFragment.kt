package com.kotlin.sacalabici

import RutasAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.RutasBase

class RutasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RutasAdapter
    private lateinit var rutasList: ArrayList<RutasBase>
    private var onRutaSelectedListener: OnRutaSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_rutas, container, false)

        recyclerView = view.findViewById(R.id.RVRutas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Inicializar el adapter vacío
        rutasAdapter = RutasAdapter(emptyList(), ::onRutaSelected)
        recyclerView.adapter = rutasAdapter

        // Obtener la lista de rutas pasada como argumento
        val rutasList = arguments?.getParcelableArrayList<RutasBase>("rutasList")
        rutasList?.let {
            updateRutasList(it)
        }

        return view
    }

    // Método para actualizar la lista de rutas en el adapter
    fun updateRutasList(rutasList: List<RutasBase>) {
        rutasAdapter.updateRutas(rutasList)
    }

    // Callback cuando una ruta es seleccionada
    private fun onRutaSelected(ruta: RutasBase) {
        onRutaSelectedListener?.onRutaSelected(ruta)
    }

    interface OnRutaSelectedListener {
        fun onRutaSelected(ruta: RutasBase)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRutaSelectedListener = context as? OnRutaSelectedListener
    }

    override fun onDetach() {
        super.onDetach()
        onRutaSelectedListener = null
    }

    companion object {
        fun newInstance(rutasList: List<RutasBase>?): RutasFragment {
            val fragment = RutasFragment()
            val args = Bundle()
            // Convierte la lista en un ArrayList antes de agregarla al Bundle
            args.putParcelableArrayList("rutasList", rutasList?.let { ArrayList(it) })
            fragment.arguments = args
            return fragment
        }
    }
}

