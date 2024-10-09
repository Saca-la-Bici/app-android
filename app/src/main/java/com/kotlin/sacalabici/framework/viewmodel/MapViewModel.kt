package com.kotlin.sacalabici.framework.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.domain.routes.DeleteRouteRequirement
import com.kotlin.sacalabici.domain.routes.PostRouteRequirement
import com.kotlin.sacalabici.domain.routes.PutRouteRequirement
import com.kotlin.sacalabici.domain.routes.RouteListRequirement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel : ViewModel() {
    val roleLiveData = MutableLiveData<String>()
    val routeObjectLiveData = MutableLiveData<List<RouteBase>?>()
    val toastMessageLiveData = MutableLiveData<String>()

    private val _selectedRuta = MutableLiveData<RouteBase>()
    val selectedRuta: LiveData<RouteBase> get() = _selectedRuta

    var lastSelectedRuta: RouteBase? = null
    private val routeListRequirement = RouteListRequirement()
    private val postRouteRequirement = PostRouteRequirement()
    private val patchRouteRequirement = PutRouteRequirement()
    private val deleteRouteRequirement = DeleteRouteRequirement()

    suspend fun processPermissions() {
        val result: RouteObjectBase? = routeListRequirement()

        // Publicar el rol en LiveData
        if (result != null) {
            this@MapViewModel.roleLiveData.postValue(result.permission.toString())
        } // Publicar los permisos en LiveData

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

    fun selectRuta(ruta: RouteBase) {
        _selectedRuta.value = ruta
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

    fun putRoute(
        id: String,
        route: Route,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                patchRouteRequirement(id, route)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun deleteRoute(
        id: String,
        route: Route,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteRouteRequirement(id, route)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
