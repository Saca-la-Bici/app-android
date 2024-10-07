package com.kotlin.sacalabici.domain.modifyRole

import android.util.Log
import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.data.repositories.ModifyRoleRepository

class PatchModifyRoleRequirements {
    private val repository = ModifyRoleRepository()

    suspend operator fun invoke(
    userId: BriefUser,
    rolId: String,
): BriefUser? {
    Log.d("PatchModifyRoleRequirements", "Patching role for user: ${userId.id} to role: $rolId")
    val result = repository.patchRole(userId, rolId)
    Log.d("PatchModifyRoleRequirements", "Role patched successfully for user: ${userId.id}")
    return result
    }
}