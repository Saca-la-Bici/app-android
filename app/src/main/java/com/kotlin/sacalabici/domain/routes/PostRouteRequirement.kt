package com.kotlin.sacalabici.domain.routes

import com.kotlin.sacalabici.data.models.routes.Route
import com.kotlin.sacalabici.data.repositories.routes.RouteRepository

class PostRouteRequirement {
    private val repository = RouteRepository()

    suspend operator fun invoke(route: Route): Route? =
        repository.postRoute(route)
}