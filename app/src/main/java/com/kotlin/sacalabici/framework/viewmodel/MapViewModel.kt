package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import android.widget.EditText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.BuildConfig
import com.kotlin.sacalabici.data.models.activities.RodadaInfoBase
import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.kotlin.sacalabici.domain.routes.PostRouteRequirement
import com.kotlin.sacalabici.domain.routes.PutRouteRequirement
import com.kotlin.sacalabici.domain.routes.RouteListRequirement
import com.kotlin.sacalabici.domain.routes.RouteRequirement
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

    val roleLiveData = MutableLiveData<String>()
    val routeObjectLiveData = MutableLiveData<List<RouteBase>?>()
    val toastMessageLiveData = MutableLiveData<String>()
    var lastSelectedRuta: RouteBase? = null
    private val routeListRequirement = RouteListRequirement()
    private val postRouteRequirement = PostRouteRequirement()
    private val patchRouteRequirement = PutRouteRequirement()

    suspend fun processPermissions() {

        val result: RouteObjectBase? = routeListRequirement()

        // Publicar el rol en LiveData
        if (result != null) {
            this@MapViewModel.roleLiveData.postValue(result.permission.toString())
        }  // Publicar los permisos en LiveData

        // Iterar sobre la lista de permisos y mostrar cada uno en Logcat
        if (result != null) {
            for (permission in result.permission) {
                Log.d("Permisos", "Permiso encontrado: $permission")
            }
        }
    }

    fun getRouteList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Obtener el objeto RouteObjectBase en lugar de solo la lista de rutas
                val result: RouteObjectBase? = routeListRequirement()

                // Publicar las rutas en LiveData
                val reversedRoutes = result!!.routes.reversed()
                this@MapViewModel.routeObjectLiveData.postValue(reversedRoutes)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toastMessageLiveData.postValue("Error al cargar la lista de rutas")
                }
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
