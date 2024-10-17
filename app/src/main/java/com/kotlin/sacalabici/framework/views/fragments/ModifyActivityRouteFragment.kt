package com.kotlin.sacalabici.framework.views.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.databinding.FragmentActivityRouteBinding
import com.kotlin.sacalabici.framework.RouteAdapter
import com.kotlin.sacalabici.framework.viewmodel.ActivitiesViewModel
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.fragments.AddActivityRouteFragment.OnRutaConfirmListener
import com.kotlin.sacalabici.framework.views.fragments.AddActivityRouteFragment.OnRutaSelectedListener


class ModifyActivityRouteFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var rutasAdapter: RouteAdapter

    private var onRutaSelectedListener: OnRutaSelectedListener? = null
    private var onRutaConfirmListener: OnRutaConfirmListener? = null
    private var lastSelectedRuta: RouteBase? = null

    private lateinit var viewModelRoute: MapViewModel
    private val activitiesViewModel: ActivitiesViewModel by activityViewModels()

    private var _binding: FragmentActivityRouteBinding? = null
    private val binding get() = _binding!!

    /*
    * Permite que el fragmento se comunique con la actividad
    * Le notificará cuando se seleccione el botón "Listo"
    * Llama a la función para recibir la información de la ruta
    * */
    interface OnRutaConfirmListener {
        fun onRutaConfirmed(rutaID: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onRutaConfirmListener = context as? OnRutaConfirmListener
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

        rutasAdapter = RouteAdapter(emptyList(), { ruta ->
            onRutaSelected(ruta)
        }, permit = false)
        recyclerView.adapter = rutasAdapter

        initializeListeners()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelRoute = ViewModelProvider(requireActivity()).get(MapViewModel::class.java)

        // Observa los LiveData del ViewModel
        viewModelRoute.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
            rutasList?.let {
                val idRoute = arguments?.getString("idRoute")
                val selectedRuta = it.find { ruta -> ruta.id == idRoute }
                updateRutasList(it, selectedRuta)
            }
        })

        val rutasList = arguments?.getParcelableArrayList<RouteBase>("rutasList")
        val selectedRuta = arguments?.getParcelable<RouteBase>("selectedRuta")

        rutasList?.let {
            updateRutasList(it, selectedRuta)
        }

        // Animación de carga
        activitiesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    fun updateRutasList(rutasList: List<RouteBase>, selectedRuta: RouteBase?) {
        rutasAdapter.updateRutas(rutasList)
        this.lastSelectedRuta = selectedRuta
        rutasAdapter.setSelectedRuta(selectedRuta)
        if (selectedRuta != null) {
            viewModelRoute.selectRuta(selectedRuta)
        }
    }

    private fun onRutaSelected(ruta: RouteBase) {
        onRutaSelectedListener?.onRutaSelected(ruta)
    }

    interface OnRutaSelectedListener {
        fun onRutaSelected(ruta: RouteBase)
    }

    companion object {
        fun newInstance(rutasList: List<RouteBase>?, idRoute: String?): ModifyActivityRouteFragment {
            val fragment = ModifyActivityRouteFragment()
            val args = Bundle()
            args.putParcelableArrayList("rutasList", rutasList?.let { ArrayList(it) })
            args.putString("idRoute", idRoute)
            fragment.arguments = args
            return fragment
        }
    }

    private fun initializeListeners() {
        binding.btnNext.setOnClickListener {
            val LastSelectedRuta = viewModelRoute.selectedRuta.value
            LastSelectedRuta?.let { ruta ->
                onRutaConfirmListener?.onRutaConfirmed(ruta.id)
            }
        }

        // Regresar al fragmento anterior
        binding.ibBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onDetach() {
        super.onDetach()
        onRutaSelectedListener = null
    }
}