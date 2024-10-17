/**
 * File: ModifyRoleViewModel.kt
 * Description: Esta clase es un ViewModel que maneja la lógica de modificación de roles de usuarios en la aplicación.
 *              Utiliza coroutines para realizar solicitudes asíncronas que modifican los roles de los usuarios.
 * Date: 16/10/2024
 * Changes: Se agregó la función patchRole para realizar el cambio de rol de un usuario.
 */

package com.kotlin.sacalabici.framework.adapters.viewmodel.modifyRole

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlin.sacalabici.data.models.user.BriefUser
import com.kotlin.sacalabici.domain.modifyRole.PatchModifyRoleRequirements
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la modificación de roles de usuarios. Proporciona un método para
 * enviar una solicitud de modificación de rol utilizando coroutines para el manejo asíncrono.
 */
class ModifyRoleViewModel : ViewModel() {
    // val modifyObjectLiveData = MutableLiveData<List<ConsultarUsuariosBase>>()

    // Instancia de los requisitos necesarios para modificar un rol
    private val modifyRoleRequirements = PatchModifyRoleRequirements()

    /**
     * Lanza una coroutine para modificar el rol de un usuario en el servidor.
     * Utiliza el Dispatcher.IO para operaciones de entrada/salida.
     *
     * @param userId Usuario cuyo rol será modificado.
     * @param rolId ID del nuevo rol que se asignará al usuario.
     * @throws Exception Si ocurre algún error durante el proceso de modificación del rol.
     */
    fun patchRole(
        userId: BriefUser,
        rolId: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Llamada a la capa de dominio para modificar el rol del usuario
                modifyRoleRequirements(userId, rolId)
            } catch (e: Exception) {
                // Lanza la excepción si ocurre un error
                throw e
            }
        }
    }
}
