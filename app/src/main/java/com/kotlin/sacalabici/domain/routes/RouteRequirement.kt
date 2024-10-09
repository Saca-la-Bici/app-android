package com.kotlin.sacalabici.domain.routes

import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.models.routes.RouteInfoObjectBase
import com.kotlin.sacalabici.data.repositories.routes.RouteRepository

class RouteRequirement {
    private val repository = RouteRepository()

    suspend operator fun invoke(id: String): RouteInfoObjectBase? = repository.getRoute(id)
}