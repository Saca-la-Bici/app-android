package com.kotlin.sacalabici.data.network.routes

import com.kotlin.sacalabici.data.models.RutasBase
import com.mapbox.geojson.Point
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface RouteApiService {
    @GET("mapa/consultarRutas")
    suspend fun getRutasList(): List<RutasBase>

    @PUT("mapa/modificarRuta/{id}")
    suspend fun modifyRoute(
        @Path("id") id: String,
        @Body routeDetails: RutasBase
    ): Response<Void>

    @POST("mapa/registrarRuta")
    suspend fun sendRoute(
        @Body routeDetails: RutasBase
    ): Response<Void>
}


