package com.kotlin.sacalabici.data.network.modificateRole

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ModifyRoleAPIService {
    // http://localhost:8080/perfil/modificarRol/{id}
    @PATCH("perfil/modificarRol/{id}")
    suspend fun patchUserRole(
        @Path("id") id: String,
        @Body body: Map<String, Any>,
    ): Response<Unit>
}