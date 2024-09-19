package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import retrofit2.Call
import retrofit2.http.GET

interface ConsultarUsuariosAPIService {
    // Define la ruta del endpoint para obtener todos los usuarios
    @GET("/perfil/consultarUsuarios")
    suspend fun getUsuarios(): Call<List<ConsultarUsuariosBase>>
}