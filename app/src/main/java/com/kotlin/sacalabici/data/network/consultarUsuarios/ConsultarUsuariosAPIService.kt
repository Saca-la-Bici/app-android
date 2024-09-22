package com.kotlin.sacalabici.data.network.consultarUsuarios

import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.models.ConsultarUsuariosObject
import retrofit2.http.GET
import retrofit2.http.Query

interface ConsultarUsuariosAPIService {
    //http://localhost:8080/perfil/consultarUsuarios
    @GET("perfil/consultarUsuarios")
    suspend fun getUsuarios(
        @Query("limit") limit: Int
    ): ConsultarUsuariosObject

}