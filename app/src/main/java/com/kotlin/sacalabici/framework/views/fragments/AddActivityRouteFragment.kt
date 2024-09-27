package com.kotlin.sacalabici.framework.views.fragments

import RutasAdapter
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.RutasBase
import com.kotlin.sacalabici.databinding.FragmentActivityRouteBinding
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.views.fragments.AddActivityInfoFragment.OnFragmentInteractionListener

class AddActivityRouteFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RutasAdapter
    private lateinit var rutasList: ArrayList<RutasBase>
    private var onRutaSelectedListener: OnRutaSelectedListener? = null
    private var lastSelectedRuta: RutasBase? = null

    private var _binding: FragmentActivityRouteBinding? = null
    private val binding get() = _binding!!
    private lateinit var listener: OnRutaConfirmListener

    /*
    * Permite que el fragmento se comunique con la actividad
    * Le notificará cuando se seleccione el botón "Listo"
    * Llama a la función para recibir la información de la ruta
    * */
    interface OnRutaConfirmListener {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRutaSelectedListener = context as? OnRutaSelectedListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActivityRouteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.RVRutas
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

        initializeListeners()

        return root
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

    private fun initializeListeners() {
        binding.btnNext.setOnClickListener {
            // Lógica para agregar una nueva ruta
        }
    }

    override fun onDetach() {
        super.onDetach()
        onRutaSelectedListener = null
    }
}