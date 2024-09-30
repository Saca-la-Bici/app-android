package com.kotlin.sacalabici.data.network.routes

import com.kotlin.sacalabici.data.models.routes.CoordenatesBase
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.announcements.AnnouncementNetworkModuleDI
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import com.mapbox.geojson.Point

class RouteApiClient(private val firebaseTokenManager: FirebaseTokenManager) {

    private lateinit var api: RouteApiService

    // Obtener la lista de rutas
    suspend fun getRutasList(): RouteObjectBase? {
        val token = firebaseTokenManager.getTokenSynchronously() // Obtener el token de forma sincrónica
        api = RouteNetworkModuleDI(token)
        return try {
            api.getRutasList()
        } catch (e:java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun postRoute(route: Route): Route? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = RouteNetworkModuleDI(token)
        return try {
            api.postRoute(route)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun modifyRoute(id: String, route: Route): Route? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = RouteNetworkModuleDI(token)
        return try {
            api.modifyRoute(id, route)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
