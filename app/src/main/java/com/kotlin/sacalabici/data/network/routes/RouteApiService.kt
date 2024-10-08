package com.kotlin.sacalabici.data.network.routes

import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.data.network.announcements.model.announcement.Announcement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApiService {
    @GET("mapa/consultarRutas")
    suspend fun getRutasList(): RouteObjectBase

    @POST("mapa/registrarRuta")
    suspend fun postRoute(@Body route: Route): Route

    @PUT("mapa/modificarRuta/{id}")
    suspend fun modifyRoute(
        @Path("id") id: String,
        @Body route: Route
    ): Route

    @GET("mapa/consultarRutas")
    suspend fun getRuta(@Query("id") id: String): Route
}