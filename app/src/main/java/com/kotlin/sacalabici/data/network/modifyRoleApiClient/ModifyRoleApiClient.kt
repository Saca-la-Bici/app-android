package com.kotlin.sacalabici.data.network.modificateRole

import android.util.Log
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
                    "rolId" to rolId,
                )
            val response = api.patchUserRole(userId.id, requestBody)
            if (response.isSuccessful) {
                Log.d("ModifyRoleApiClient", "Role patched successfully for user: ${userId.id}")
                userId
            } else {
                Log.e("ModifyRoleApiClient", "Failed to patch role for user: ${userId.id}")
                null
            }
        } catch (e: Exception) {
            Log.e("ModifyRoleApiClient", "Error patching user role", e)
            null
        }
    }
}