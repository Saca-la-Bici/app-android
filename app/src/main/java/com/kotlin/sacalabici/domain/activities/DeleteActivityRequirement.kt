package com.kotlin.sacalabici.domain.activities

import com.kotlin.sacalabici.data.repositories.activities.DeleteActivityRepository

class DeleteActivityRequirement {
    private val repository = DeleteActivityRepository()

    suspend operator fun invoke(
        id: String,
        typeAct: String
    ): Boolean {
        return repository.deleteActivity(id, typeAct)
    }
}