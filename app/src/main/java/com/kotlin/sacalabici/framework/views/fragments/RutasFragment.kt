package com.kotlin.sacalabici.framework.views.fragments

import RutasAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.RutasBase

class RutasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RutasAdapter
    private lateinit var rutasList: ArrayList<RutasBase>
    private var onRutaSelectedListener: OnRutaSelectedListener? = null
    private var lastSelectedRuta: RutasBase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lista_rutas, container, false)

        recyclerView = view.findViewById(R.id.RVRutas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        rutasAdapter = RutasAdapter(emptyList()) { ruta ->
            onRutaSelected(ruta)
        }
        recyclerView.adapter = rutasAdapter

        val rutasList = arguments?.getParcelableArrayList<RutasBase>("rutasList")
        val selectedRuta = arguments?.getParcelable<RutasBase>("selectedRuta")
        rutasList?.let {
            updateRutasList(it, selectedRuta)
        }

        return view
    }

    fun updateRutasList(rutasList: List<RutasBase>, selectedRuta: RutasBase?) {
        rutasAdapter.updateRutas(rutasList)
        this.lastSelectedRuta = selectedRuta
        rutasAdapter.setSelectedRuta(selectedRuta)
    }

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
        fun newInstance(rutasList: List<RutasBase>?, selectedRuta: RutasBase?): RutasFragment {
            val fragment = RutasFragment()
            val args = Bundle()
            args.putParcelableArrayList("rutasList", rutasList?.let { ArrayList(it) })
            args.putParcelable("selectedRuta", selectedRuta)
            fragment.arguments = args
            return fragment
        }
    }
}
