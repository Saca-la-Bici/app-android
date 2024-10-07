package com.kotlin.sacalabici.framework.views.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.AddRouteActivity
import com.kotlin.sacalabici.helpers.MapHelper
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class MapFragment: Fragment(), RutasFragment.OnRutaSelectedListener {

    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private lateinit var mapView: MapView

    // Para almacenar las fuentes y capas de rutas
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    // Para almacenar las fuentes y capas de pines
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

    private var lastSelectedRuta: RouteBase? = null

    private var isRutasFragmentVisible = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapViewMap

        // Inicializa el mapa
        initializeMap()

        // Observa los cambios en los LiveData del ViewModel
        observeViewModel()

        // Configura los listeners de los botones
        setupListeners()

        lifecycleScope.launch {
            // Procesar permisos en el ViewModel
            viewModel.processPermissions()
        }

        //binding.btnAdd.visibility = View.GONE
        //binding.btnDetails.visibility = View.GONE

        // Observar los cambios en los permisos
        viewModel.roleLiveData.observe(this) { permisos ->
            Log.d("PermisosUsuario", "Permisos obtenidos: $permisos")

            // Cambiar visibilidad del botón basado en los permisos obtenidos
            if (permisos.contains("Registrar ruta")) {
                binding?.btnAdd?.visibility = View.VISIBLE
            } else {
                binding?.btnAdd?.visibility = View.GONE
            }

            if (permisos.contains("Consultar ruta")) {
                binding?.btnDetails?.visibility = View.VISIBLE
            } else {
                binding?.btnDetails?.visibility = View.GONE
            }
        }

        return root
    }

    companion object {
        private const val REQUEST_CODE_ADD_ROUTE = 1
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddRouteActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_ROUTE)
        }

        binding.btnDetails.setOnClickListener {
            toggleRutasList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_ROUTE && resultCode == AppCompatActivity.RESULT_OK) {
            // Llamar al método para obtener la lista de rutas nuevamente
            viewModel.getRouteList() // Esto activará la observación en el ViewModel
        }
    }

    private fun observeViewModel() {
        // Observamos las rutas del ViewModel
        viewModel.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
            if (rutasList.isNullOrEmpty()) {
                // Si no hay rutas, mostramos el Toast
                showToast("No se encontraron rutas")
                Log.d("ObserveViewModel", "No se encontraron rutas")
            } else {
                // Si hay rutas disponibles, verificamos si el fragmento ya está visible
                val rutasFragment = childFragmentManager.findFragmentById(R.id.fragment_container)

                if (rutasFragment == null) {
                    // Si el fragmento no está visible, lo añadimos
                    val newRutasFragment = RutasFragment.newInstance(rutasList, viewModel.lastSelectedRuta)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newRutasFragment) // Reemplazamos el fragmento existente
                        .addToBackStack(null)
                        .commit()

                    // Actualizamos la visibilidad del fragmento
                    isRutasFragmentVisible = true
                    Log.d("ObserveViewModel", "Fragmento RutasFragment añadido")
                }
            }
        })

        // Observamos los mensajes de Toast
        viewModel.toastMessageLiveData.observe(viewLifecycleOwner, Observer { message ->
            showToast(message)
        })
    }

    private fun initializeMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            val queretaroCoordinates = Point.fromLngLat(-100.4091, 20.5925)
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(queretaroCoordinates)
                    .zoom(15.0)
                    .build()
            )
        }
        mapView = binding.mapViewMap
    }

    private fun toggleRutasList() {
        val fragmentManager = childFragmentManager
        val rutasFragment = fragmentManager.findFragmentById(R.id.fragment_container)

        Log.d("ToggleRutas", rutasFragment.toString())

        if (isRutasFragmentVisible) {
            // Si el fragmento ya está visible, lo eliminamos
            if (rutasFragment != null) {
                childFragmentManager.beginTransaction()
                    .remove(rutasFragment)
                    .addToBackStack(null)
                    .commit()
                Log.d("ToggleRutas", "Fragmento RutasFragment eliminado")
            }
            isRutasFragmentVisible = false
        } else {
            // Si el fragmento no está visible, primero obtén las rutas y luego agrégalo
            viewModel.getRouteList() // Esto activará la observación y añadirá el fragmento cuando las rutas se carguen
            viewModel.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
                if (rutasList.isNullOrEmpty()) {
                    showToast("No se encontraron rutas")
                } else {
                    val newRutasFragment = RutasFragment.newInstance(rutasList, viewModel.lastSelectedRuta)
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, newRutasFragment)
                        .addToBackStack(null)
                        .commit()
                    isRutasFragmentVisible = true
                    Log.d("ToggleRutas", "Fragmento RutasFragment añadido")
                }
            })
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onRutaSelected(ruta: RouteBase) {
        lastSelectedRuta = ruta

        mapView = binding.mapViewMap

        clearPreviousRoutes()

        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(20.0)
                .build()
        )

        val mapHelper = MapHelper(requireContext())

        mapHelper.drawRouteWithCoordinates(mapView,ruta.coordenadas)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun clearPreviousRoutes() {
        mapView.getMapboxMap().getStyle { style ->
            // Eliminar las fuentes de rutas anteriores
            routeSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            routeSources.clear() // Limpiamos la lista de fuentes

            // Eliminar las capas de rutas anteriores
            routeLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            routeLayers.clear() // Limpiamos la lista de capas

            // Eliminar las fuentes de pines anteriores
            pinSources.forEach { sourceId ->
                style.getSourceAs<GeoJsonSource>(sourceId)?.let {
                    style.removeStyleSource(sourceId)
                }
            }
            pinSources.clear() // Limpiamos la lista de fuentes de pines

            // Eliminar las capas de pines anteriores
            pinLayers.forEach { layerId ->
                style.getLayer(layerId)?.let {
                    style.removeStyleLayer(layerId)
                }
            }
            pinLayers.clear() // Limpiamos la lista de capas de pines
        }
    }
}
