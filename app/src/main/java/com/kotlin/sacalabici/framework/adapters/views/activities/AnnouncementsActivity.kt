package com.kotlin.sacalabici.framework.adapters.views.activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.ActivityAnnouncementsBinding
import com.kotlin.sacalabici.framework.adapters.AnnouncementAdapter
import com.kotlin.sacalabici.framework.adapters.viewmodel.AnnouncementsViewModel

class AnnouncementsActivity: BaseActivity() {
    private lateinit var binding: ActivityAnnouncementsBinding
    private lateinit var recyclerView: RecyclerView
    private val adapter : AnnouncementAdapter = AnnouncementAdapter()
    //private lateinit var data:ArrayList<AnnouncementBase>
    private lateinit var viewModel: AnnouncementsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        val root: View = binding.root

        //data = ArrayList()
        initializeComponents(root)
        initializeObservers()
        setupNavbar()
        viewModel.getAnnouncementList()
    }

    private fun initializeObservers() {
        viewModel.announcementObjectLiveData.observe(this) { announcementList ->
            setUpRecyclerView(ArrayList(announcementList))
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVAnnouncements)
    }

    private fun initializeBinding(){
        binding = ActivityAnnouncementsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setUpRecyclerView(dataForList:ArrayList<AnnouncementBase>){
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false)
        recyclerView.layoutManager = linearLayoutManager
        adapter.AnnouncementAdapter(dataForList,this)
        recyclerView.adapter = adapter
    }
}