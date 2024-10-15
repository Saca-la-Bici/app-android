package com.kotlin.sacalabici.framework.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.AddRouteActivity
import com.kotlin.sacalabici.helpers.MapHelper
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.launch

class MapFragment: Fragment(), RouteFragment.OnRutaSelectedListener {

    // Variable para vincular el archivo de diseño XML
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    // ViewModel que contiene la lógica del mapa
    private val viewModel: MapViewModel by viewModels()

    // Variable para el mapa
    private lateinit var mapView: MapView

    // Listas para almacenar fuentes y capas de rutas y pines
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

    // Última ruta seleccionada
    private var lastSelectedRuta: RouteBase? = null

    // Indica si el fragmento de rutas está visible
    private var isRutasFragmentVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicializa el binding para vincular el diseño XML
        _binding = ActivityMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Inicializa el mapa
        mapView = binding.mapViewMap
        initializeMap()

        // Observa cambios en los LiveData del ViewModel
        observeViewModel()

        // Configura los listeners de los botones
        setupListeners()

        // Procesa permisos en el ViewModel
        lifecycleScope.launch {
            viewModel.processPermissions()
        }

        // Configura visibilidad inicial de botones
        binding.btnAdd.visibility = View.GONE
        binding.btnDetails.visibility = View.GONE

        // Observa cambios en los permisos
        viewModel.roleLiveData.observe(viewLifecycleOwner) { permisos ->
            // Basado en los permisos obtenidos, muestra u oculta botones
            if (permisos.contains("Registrar ruta")) {
                binding.btnAdd.visibility = View.VISIBLE
            } else {
                binding.btnAdd.visibility = View.GONE
            }

            if (permisos.contains("Consultar ruta")) {
                binding.btnDetails.visibility = View.VISIBLE
            } else {
                binding.btnDetails.visibility = View.GONE
            }
        }

        return root
    }

    companion object {
        private const val REQUEST_CODE_ADD_ROUTE = 1
    }

    // Configura listeners para los botones
    private fun setupListeners() {
        // Listener para el botón de agregar ruta
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddRouteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ROUTE)
        }

        // Listener para el botón de detalles de la ruta
        binding.btnDetails.setOnClickListener {
            toggleRutasList()
        }

        binding.btnMessage.setOnClickListener{

        }
    }

    // Maneja el resultado de agregar una ruta
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_ROUTE && resultCode == AppCompatActivity.RESULT_OK) {
            // Vuelve a cargar la lista de rutas
            viewModel.getRouteList()
        }
    }

    // Observa los LiveData en el ViewModel
    private fun observeViewModel() {
        // Observa la lista de rutas
        viewModel.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
            if (rutasList.isNullOrEmpty()) {
                // Si no hay rutas, muestra un mensaje de aviso
                showToast("No se encontraron rutas")
            } else {
                // Si hay rutas, muestra el fragmento de rutas si no está visible
                val rutasFragment = childFragmentManager.findFragmentById(R.id.fragment_container)

                if (rutasFragment == null) {
                    // Si el fragmento no está visible, lo añadimos
                    val newRutasFragment = RouteFragment.newInstance(rutasList, viewModel.lastSelectedRuta)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newRutasFragment) // Reemplazamos el fragmento existente
                        .addToBackStack(null)
                        .commit()
                    // Actualizamos la visibilidad del fragmento
                    isRutasFragmentVisible = true
                }
            }
        })

        // Observamos los mensajes de Toast
        viewModel.toastMessageLiveData.observe(viewLifecycleOwner, Observer { message ->
            showToast(message)
        })
    }

    // Inicializa el mapa con estilo y ubicación inicial
    private fun initializeMap() {
        // Carga el estilo del mapa de Mapbox y establece la cámara en una ubicación inicial
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            // Coordenadas de Querétaro para centrar el mapa
            val queretaroCoordinates = Point.fromLngLat(-100.4091, 20.5925)
            // Configura la cámara del mapa
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(15.0)
                    .build()
            )
        }
        mapView = binding.mapViewMap
    }

    // Alterna la visibilidad del fragmento de lista de rutas
    private fun toggleRutasList() {
        val fragmentManager = childFragmentManager
        val rutasFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        if (isRutasFragmentVisible) {
            // Si el fragmento está visible, lo elimina
            rutasFragment?.let {
                childFragmentManager.beginTransaction()
                    .remove(it)
                    .addToBackStack(null)
                    .commit()
            }
            isRutasFragmentVisible = false
        } else {
            // Si no está visible, carga las rutas y muestra el fragmento
            viewModel.getRouteList()
            viewModel.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
                if (!rutasList.isNullOrEmpty()) {
                    val newRutasFragment = RouteFragment.newInstance(rutasList, viewModel.lastSelectedRuta)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newRutasFragment)
                        .addToBackStack(null)
                        .commit()
                    isRutasFragmentVisible = true
                }
            })
        }
    }

    // Muestra un mensaje Toast en pantalla
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Maneja la selección de una ruta desde el fragmento de rutas
    override fun onRutaSelected(ruta: RouteBase) {
        // Guarda la última ruta seleccionada
        lastSelectedRuta = ruta

        mapView = binding.mapViewMap

        // Limpia rutas previas antes de dibujar la nueva ruta seleccionada
        clearPreviousRoutes()

        // Obtiene la primera coordenada de la ruta para centrar el mapa
        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        // Ajusta la cámara del mapa para centrarla en la ruta seleccionada
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(20.0)
                .build()
        )

        // Utiliza MapHelper para dibujar la ruta en el mapa
        val mapHelper = MapHelper(requireContext())
        mapHelper.drawRouteWithCoordinates(mapView, ruta.coordenadas)
    }

    // Limpia el binding al destruir la vista
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Elimina las rutas previas del mapa
    fun clearPreviousRoutes() {
        // Accede al estilo del mapa y elimina todas las fuentes y capas anteriores relacionadas con rutas y pines
        mapView.getMapboxMap().getStyle { style ->
            // Eliminar las fuentes de rutas anteriores
            routeSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            routeSources.clear()

            // Eliminar las capas de rutas anteriores
            routeLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            routeLayers.clear()

            // Eliminar las fuentes de pines anteriores
            pinSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            pinSources.clear()

            // Eliminar las capas de pines anteriores
            pinLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            pinLayers.clear()
        }
    }
}
