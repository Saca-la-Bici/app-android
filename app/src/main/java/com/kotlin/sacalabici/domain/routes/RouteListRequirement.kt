package com.kotlin.sacalabici.domain.routes


import com.kotlin.sacalabici.data.models.routes.RouteBase
import com.kotlin.sacalabici.data.models.routes.RouteObjectBase
import com.kotlin.sacalabici.data.repositories.routes.RouteRepository

class RouteListRequirement {
    private val repository = RouteRepository()

    suspend operator fun invoke(
    ): RouteObjectBase? = repository.getRouteList()

}

