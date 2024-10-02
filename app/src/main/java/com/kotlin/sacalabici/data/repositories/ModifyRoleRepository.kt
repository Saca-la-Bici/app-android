package com.kotlin.sacalabici.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.data.network.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.modificateRole.ModifyRoleApiClient

class ModifyRoleRepository {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseTokenManager = FirebaseTokenManager(firebaseAuth)
    private val apiModifyRole = ModifyRoleApiClient(firebaseTokenManager)

    suspend fun patchRole(
        userId: BriefUser,
        rolId: String,
    ): BriefUser? = apiModifyRole.patchUserRole(userId, rolId)
}