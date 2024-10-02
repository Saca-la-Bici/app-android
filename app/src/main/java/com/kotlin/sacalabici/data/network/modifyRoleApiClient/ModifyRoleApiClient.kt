package com.kotlin.sacalabici.data.network.modificateRole

import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.data.network.FirebaseTokenManager

class ModifyRoleApiClient(
    private val firebaseTokenManager: FirebaseTokenManager,
) {
    private lateinit var api: ModifyRoleAPIService

    suspend fun patchUserRole(
        userId: BriefUser,
        rolId: String,
    ): BriefUser? {
        val token = firebaseTokenManager.getTokenSynchronously()
        api = ModifyRoleNetworkModuleDI(token)

        return try {
            val requestBody =
                mapOf(
                    "userid" to userId.id,
                )
            val response = api.patchUserRole(userId.id, requestBody)
            if (response.isSuccessful) {
                userId
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}