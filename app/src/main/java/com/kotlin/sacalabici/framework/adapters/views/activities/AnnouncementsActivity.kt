package com.kotlin.sacalabici.framework.adapters.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var addAnnouncementLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        val root: View = binding.root

        initializeComponents(root)
        initializeObservers()
        setupNavbar()
        setupClickListeners()
        viewModel.getAnnouncementList()

        addAnnouncementLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Refresh announcements after returning from AddAnnouncementActivity
                viewModel.getAnnouncementList()
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddAnouncement.setOnClickListener {
            passToAddActivity(this)
        }
    }

    private fun passToAddActivity(context: Context) {
        var intent = Intent(context, AddAnnouncementActivity::class.java)
        addAnnouncementLauncher.launch(intent)
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