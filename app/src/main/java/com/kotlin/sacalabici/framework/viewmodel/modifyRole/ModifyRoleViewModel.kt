package com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.domain.modifyRole.PatchModifyRoleRequirements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ModifyRoleViewModel : ViewModel() {
    // val modifyObjectLiveData = MutableLiveData<List<ConsultarUsuariosBase>>()
    private val modifyRoleRequirements = PatchModifyRoleRequirements()

    fun patchRole(
        userId: BriefUser,
        rolId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("ModifyRoleViewModel", "Patching role for user: ${userId.id} to role: $rolId")
                modifyRoleRequirements(userId, rolId)
                Log.d("ModifyRoleViewModel", "Role patched successfully for user: ${userId.id}")
            } catch (e: Exception) {
                Log.e("ModifyRoleViewModel", "Error patching role for user: ${userId.id}", e)
                throw e
            }
        }
    }
}


