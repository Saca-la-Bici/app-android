package com.kotlin.sacalabici.data.repositories.routes

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.routes.RouteApiClient

class RouteRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiRoute = RouteApiClient(firebaseTokenManager)

    suspend fun getRouteList(): RouteObjectBase? = apiRoute.getRutasList()

    suspend fun postRoute(route: Route): Route? = apiRoute.postRoute(route)

    suspend fun putRoute(
        id: String,
        route: Route,
    ): Route? = apiRoute.modifyRoute(id, route)

    suspend fun deleteRoute(id: String): RouteBase? {
        return apiRoute.deleteRoute(id)
    }
}
