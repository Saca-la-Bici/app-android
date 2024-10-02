package com.kotlin.sacalabici.domain.modifyRole

import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.data.repositories.ModifyRoleRepository

class PatchModifyRoleRequirements {
    private val repository = ModifyRoleRepository()

    suspend operator fun invoke(
        userId: BriefUser,
        rolId: String,
    ): BriefUser? = repository.patchRole(userId, rolId)
}