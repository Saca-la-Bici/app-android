package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository
import com.kotlin.sacalabici.databinding.ActivityModifyRolBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConsultarUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyRolBinding
    private val adapter : ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private lateinit var data:ArrayList<ConsultarUsuariosBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()

        // Llamar a la función para obtener los usuarios
        getUsuarios()
    }

    private fun initializeBinding(){
        binding = ActivityModifyRolBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun getUsuarios() {
        CoroutineScope(Dispatchers.IO).launch {
            val consultarUsuariosRepository = ConsultarUsuariosRepository()

            try {
                val usuarios = consultarUsuariosRepository.getUsuarios(limit = 0)

                withContext(Dispatchers.Main) {
                    if (!usuarios.isNullOrEmpty()) {
                        setUpRecyclerView(ArrayList(usuarios))
                    } else {
                        Log.d("Error", "La lista de usuarios está vacía o es nula.")
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Error", "No se pudieron obtener los usuarios: ${e.message}")
            }
        }
    }

    private fun setUpRecyclerView(dataForList:ArrayList<ConsultarUsuariosBase>){
        binding.RVViewUsers.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false)
        binding.RVViewUsers.layoutManager = linearLayoutManager
        adapter.ConsultarUsuariosAdapter(dataForList)
        binding.RVViewUsers.adapter = adapter
    }
}
