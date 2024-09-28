package com.kotlin.sacalabici.framework.viewmodel

import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.domain.routes.PostRouteRequirement
import com.kotlin.sacalabici.domain.routes.PutRouteRequirement
import com.kotlin.sacalabici.domain.routes.RouteListRequirement
import com.mapbox.geojson.Point
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import kotlinx.coroutines.Dispatchers
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

class MapViewModel : ViewModel() {

    val routeObjectLiveData = MutableLiveData<List<RouteBase>?>()
    val routeSegmentsLiveData = MutableLiveData<Pair<List<Point>, List<Point>>>()
    val toastMessageLiveData = MutableLiveData<String>()
    var lastSelectedRuta: RouteBase? = null
    private val routeListRequirement = RouteListRequirement()
    private val postRouteRequirement = PostRouteRequirement()
    private val patchRouteRequirement = PutRouteRequirement()

    private lateinit var mapView: MapView

    // Para almacenar las fuentes y capas de rutas
    private val routeSources = mutableListOf<String>()
    private val routeLayers = mutableListOf<String>()

    // Para almacenar las fuentes y capas de pines
    private val pinSources = mutableListOf<String>()
    private val pinLayers = mutableListOf<String>()

    fun getRouteList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result: List<RouteBase> = routeListRequirement()
                val reversedResult = result.reversed()
                this@MapViewModel.routeObjectLiveData.postValue(reversedResult)
            } catch (e: Exception) {
                this@MapViewModel.routeObjectLiveData.postValue(emptyList())
            }
        }
    }

    fun postRoute(route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                postRouteRequirement(route)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun putRoute(id: String, route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                patchRouteRequirement(id, route)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
