package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.sacalabici.data.models.ConsultarUsuariosBase
import com.kotlin.sacalabici.data.repositories.ConsultarUsuariosRepository
import com.kotlin.sacalabici.databinding.ActivityModifyRolBinding
import com.kotlin.sacalabici.framework.adapters.viewmodel.ConsultarUsuariosAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConsultarUsuariosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyRolBinding
    private val adapter : ConsultarUsuariosAdapter = ConsultarUsuariosAdapter()
    private lateinit var data:ArrayList<ConsultarUsuariosBase>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeBinding()
        setUpRecyclerView(testData())

        // Llamar a la función para obtener los usuarios
        //getUsuarios()
    }

    private fun initializeBinding(){
        binding = ActivityModifyRolBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun testData():ArrayList<ConsultarUsuariosBase>{
        val result = ArrayList<ConsultarUsuariosBase>()
        result.add(ConsultarUsuariosBase("Juan Perez","img1"))
        result.add(ConsultarUsuariosBase("Maria Gomez","img2"))
        result.add(ConsultarUsuariosBase("Pedro Lopez","img3"))
        result.add(ConsultarUsuariosBase("Ana Martinez","img4"))
        result.add(ConsultarUsuariosBase("Carlos Rodriguez","img5"))
        return result
    }

//    private fun getUsuarios() {
//        // Lanzar coroutine en el hilo de I/O
//        CoroutineScope(Dispatchers.IO).launch {
//            // Instancia del repositorio
//            val consultarUsuariosRepository = ConsultarUsuariosRepository()
//            // Llamada al método para obtener los usuarios
//            val usuarios = consultarUsuariosRepository.getUsuarios()
//
//            CoroutineScope(Dispatchers.Main).launch {
//                setUpRecyclerView(usuarios)
//            }
//        }
//    }

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
