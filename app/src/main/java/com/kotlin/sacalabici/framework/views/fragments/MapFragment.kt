package com.kotlin.sacalabici.framework.views.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.databinding.ActivityMapBinding
import com.kotlin.sacalabici.framework.viewmodel.MapViewModel
import com.kotlin.sacalabici.framework.views.activities.AddRouteActivity
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource

class MapFragment: Fragment(), RutasFragment.OnRutaSelectedListener {
    private var _binding: ActivityMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MapViewModel by viewModels()

    private lateinit var mapView: MapView
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    private var lastSelectedRuta: RouteBase? = null

    private var isRutasListVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = binding.mapView

        // Observa los cambios en los LiveData del ViewModel
        observeViewModel()

        // Inicializa el mapa
        initializeMap()

        // Configura los listeners de los botones
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddRouteActivity::class.java)
            startActivity(intent)
        }

        binding.btnDetails.setOnClickListener {
            toggleRutasList()
        }

        return root
    }

    private fun observeViewModel() {
        viewModel.routeObjectLiveData.observe(viewLifecycleOwner, Observer { rutasList ->
            rutasList?.let {
                // Si la lista de rutas se ha obtenido, crea el fragmento RutasFragment
                val rutasFragment = RutasFragment.newInstance(it, viewModel.lastSelectedRuta)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, rutasFragment)
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                showToast("Error al obtener la lista de rutas.")
            }
        })

        viewModel.routeSegmentsLiveData.observe(viewLifecycleOwner, Observer { routeSegments ->
            drawRouteSegments(mapView, routeSegments.first, routeSegments.second)
        })

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
    }

    private fun toggleRutasList() {
        // Cambia el estado de la lista
        isRutasListVisible = !isRutasListVisible

        if (isRutasListVisible) {
            // Si la lista es visible, obtén y muestra las rutas
            viewModel.getRouteList()
            // Aquí deberías agregar el código para mostrar la lista en la interfaz de usuario
        } else {
            // Si la lista no es visible, oculta las rutas
            // Aquí deberías agregar el código para ocultar la lista en la interfaz de usuario
        }
    }

    private fun drawRouteSegments(map: MapView, tramo1: List<Point>, tramo2: List<Point>) {
        map.getMapboxMap().getStyle { style ->
            addRouteLayer(style, tramo1, Color.RED)
            addRouteLayer(style, tramo2, Color.GREEN)
        }
    }

    private fun addRouteLayer(style: Style, points: List<Point>, color: Int) {
        val sourceId = "route-source-${System.currentTimeMillis()}"
        val layerId = "route-layer-${System.currentTimeMillis()}"

        val source = GeoJsonSource.Builder(sourceId)
            .featureCollection(
                FeatureCollection.fromFeatures(
                    listOf(Feature.fromGeometry(LineString.fromLngLats(points)))
                )
            )
            .build()

        val lineLayer = LineLayer(layerId, sourceId)

        lineLayer.lineColor(color)
        lineLayer.lineWidth(5.0)

        style.addSource(source)
        style.addLayer(lineLayer)

        // Guarda los IDs para su posterior eliminación
        routeSources.add(sourceId)
        routeLayers.add(layerId)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onRutaSelected(ruta: RouteBase) {
        lastSelectedRuta = ruta

        viewModel.clearPreviousRoutes()

        val firstCoordinate = ruta.coordenadas.firstOrNull() ?: return
        val point = Point.fromLngLat(firstCoordinate.longitud, firstCoordinate.latitud)

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(15.0)
                .build()
        )

        ruta?.let {
            viewModel.lastSelectedRuta = it
            viewModel.drawRoute(it.coordenadas)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
