package com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole

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
                modifyRoleRequirements(userId, rolId)
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
