package com.kotlin.sacalabici.framework.adapters.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.FirebaseTokenManager
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.FragmentAnnouncementsBinding
import com.kotlin.sacalabici.framework.adapters.AnnouncementAdapter
import com.kotlin.sacalabici.framework.adapters.viewmodel.AnnouncementsViewModel
import com.kotlin.sacalabici.framework.adapters.views.activities.AddAnnouncementActivity
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay

class AnnouncementsFragment: Fragment() {
    private var _binding: FragmentAnnouncementsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val adapter: AnnouncementAdapter = AnnouncementAdapter { announcement ->
        showDialog(announcement)
        true
    }
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var addAnnouncementLauncher: ActivityResultLauncher<Intent>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        val root: View = binding.root

        initializeComponents(root)
        initializeObservers()
        setupClickListeners()
        viewModel.getAnnouncementList()

        addAnnouncementLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getAnnouncementList()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupClickListeners() {
        binding.fabAddAnouncement.setOnClickListener {
            passToAddActivity(requireContext())
        }
    }

    private fun passToAddActivity(context: Context) {
        var intent = Intent(context, AddAnnouncementActivity::class.java)
        addAnnouncementLauncher.launch(intent)
    }

    private fun initializeObservers() {
        viewModel.announcementObjectLiveData.observe(viewLifecycleOwner) { announcementList ->
            setUpRecyclerView(ArrayList(announcementList))
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVAnnouncements)
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.getAnnouncementList()
        }
    }

    private fun setUpRecyclerView(dataForList:ArrayList<AnnouncementBase>){
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false)
        recyclerView.layoutManager = linearLayoutManager
        adapter.AnnouncementAdapter(dataForList,requireContext())
        recyclerView.adapter = adapter
    }

    private fun showDialog(announcement: AnnouncementBase) {
        val dialogFragment = ActionButtonDialogFragment.newInstance(
            announcement.id,
            announcement.title,
            announcement.content,
            announcement.url
        )
        dialogFragment.show(parentFragmentManager, ActionButtonDialogFragment.TAG)
    }
}

