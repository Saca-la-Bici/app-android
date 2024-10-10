package com.kotlin.sacalabici.data.network.routes

import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RouteApiService {
    @GET("mapa/consultarRutas")
    suspend fun getRutasList(): RouteObjectBase

    @POST("mapa/registrarRuta")
    suspend fun postRoute(
        @Body route: Route,
    ): Route

    @PUT("mapa/modificarRuta/{id}")
    suspend fun modifyRoute(
        @Path("id") id: String,
        @Body route: Route,
    ): Route

    @PUT("mapa/eliminarRuta/{id}")
    suspend fun deleteRoute(
        @Path("ID") id: String
    ): Response<Void>
}
