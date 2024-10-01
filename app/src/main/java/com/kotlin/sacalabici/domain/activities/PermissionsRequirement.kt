package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.models.profile.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.activities.ActivitiesRepository

class PermissionsRequirement {
    private val repository = ActivitiesRepository()

    suspend fun getPermissions(): List<String> = repository.getPermissions()
}