package com.kotlin.sacalabici.framework.views.fragments

import RutasAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.RouteBase

class RutasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RutasAdapter
    private var onRutaSelectedListener: OnRutaSelectedListener? = null
    private var lastSelectedRuta: RouteBase? = null

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

        val rutasList = arguments?.getParcelableArrayList<RouteBase>("rutasList")
        val selectedRuta = arguments?.getParcelable<RouteBase>("selectedRuta")
        rutasList?.let {
            updateRutasList(it, selectedRuta)
        }

        return view
    }

    fun updateRutasList(rutasList: List<RouteBase>, selectedRuta: RouteBase?) {
        rutasAdapter.updateRutas(rutasList)
        this.lastSelectedRuta = selectedRuta
        rutasAdapter.setSelectedRuta(selectedRuta)
    }

    interface OnRutaSelectedListener {
        fun onRutaSelected(ruta: RouteBase)
    }

    private fun onRutaSelected(ruta: RouteBase) {
        onRutaSelectedListener?.onRutaSelected(ruta)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parentFragment = requireParentFragment() // Cambiado aquí
        if (parentFragment is OnRutaSelectedListener) {
            onRutaSelectedListener = parentFragment
        } else {
            throw RuntimeException("$parentFragment must implement OnRutaSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onRutaSelectedListener = null
    }

    companion object {
        fun newInstance(rutasList: List<RouteBase>?, selectedRuta: RouteBase?): RutasFragment {
            val fragment = RutasFragment()
            val args = Bundle()
            args.putParcelableArrayList("rutasList", rutasList?.let { ArrayList(it) })
            args.putParcelable("selectedRuta", selectedRuta)
            fragment.arguments = args
            return fragment
        }
    }
}

