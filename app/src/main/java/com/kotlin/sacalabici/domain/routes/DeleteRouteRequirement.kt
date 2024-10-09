package com.kotlin.sacalabici.domain.routes

import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.repositories.routes.RouteRepository

class DeleteRouteRequirement {
    private val repository = RouteRepository()

    suspend operator fun invoke(
        id: String,
        route: Route,
    ): Route? = repository.deleteRoute(id, route)
}
