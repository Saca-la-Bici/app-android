package com.kotlin.sacalabici.framework.views.fragments

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kotlin.sacalabici.R
import com.kotlin.sacalabici.data.network.announcements.model.AnnouncementBase
import com.kotlin.sacalabici.databinding.FragmentAnnouncementsBinding
import com.kotlin.sacalabici.framework.adapters.AnnouncementAdapter
import com.kotlin.sacalabici.framework.views.activities.announcement.AddAnnouncementActivity
import kotlinx.coroutines.delay
import androidx.lifecycle.lifecycleScope
import com.kotlin.sacalabici.framework.viewmodel.AnnouncementsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnnouncementsFragment: Fragment() {
    private var _binding: FragmentAnnouncementsBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val adapter: AnnouncementAdapter = AnnouncementAdapter(
        longClickListener = { announcement: AnnouncementBase ->
            showDialog(announcement)
            true
        }
    )
    private lateinit var viewModel: AnnouncementsViewModel
    private lateinit var addAnnouncementLauncher: ActivityResultLauncher<Intent>
    private lateinit var modifyAnnouncementLauncher: ActivityResultLauncher<Intent>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var permissions: List<String> = emptyList()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAnnouncementsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AnnouncementsViewModel::class.java]
        val root: View = binding.root
        // Recuperar permisos guardados en SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("my_preferences", Context.MODE_PRIVATE)
        val storedPermissions = sharedPreferences.getStringSet("permissions", null)?.toList()

        // Mostrar botón de añadir anuncio solo si se tiene el permiso correspondiente
        if (storedPermissions?.contains("Registrar anuncio") == true) {
            binding.fabAddAnouncement.visibility = View.VISIBLE
        }
        this.permissions = storedPermissions!!

        initializeComponents(root)
        // Configurar listener para el resultado de acciones del diálogo
        setFragmentResultListener("actionButtonDialogResult") { _, bundle ->
            val resultCode = bundle.getInt("resultCode")
            if (resultCode == Activity.RESULT_OK) {
                viewModel.getAnnouncementList()
            }
        }
        initializeObservers()
        setupClickListeners()
        fetchAnnouncements()

        // Lanzador para añadir anuncios
        addAnnouncementLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.getAnnouncementList() // Actualizar lista si se añadió un anuncio
            }
        }

        // Lanzador para modificar anuncios
        modifyAnnouncementLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewModel.getAnnouncementList() // Actualizar lista si se añadió un anuncio
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        fetchAnnouncements() // Obtener la lista de anuncios cada vez que el fragmento se reanuda
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
        val intent = Intent(context, AddAnnouncementActivity::class.java)
        addAnnouncementLauncher.launch(intent)
    }


    private fun initializeObservers() {
        viewModel.announcementObjectLiveData.observe(viewLifecycleOwner) { announcementList ->
            if (announcementList.isEmpty()) {
                binding.tvNoAnnouncements.visibility = View.VISIBLE
            } else {
                binding.tvNoAnnouncements.visibility = View.GONE
            }
            lifecycleScope.launch {
                delay(50)
                setUpRecyclerView(ArrayList(announcementList))
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun initializeComponents(root: View) {
        recyclerView = root.findViewById(R.id.RVAnnouncements)
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchAnnouncements()
        }
    }

    private fun fetchAnnouncements() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getAnnouncementList()
        }
    }

    private fun setUpRecyclerView(dataForList: ArrayList<AnnouncementBase>) {
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.layoutManager = linearLayoutManager
        adapter.AnnouncementAdapter(dataForList, requireContext())
        recyclerView.adapter = adapter
    }

    private fun showDialog(announcement: AnnouncementBase) {
        // Mostrar diálogo para modificar o eliminar anuncio si se tienen permisos
        if (permissions.contains("Modificar anuncio") || permissions.contains("Eliminar anuncio")) {
            val dialogFragment = ActionButtonDialogFragment.newInstance(
                announcement.id,
                announcement.title,
                announcement.content,
                announcement.url,
                permissions // Pasar permisos al diálogo
            )
            dialogFragment.show(parentFragmentManager, ActionButtonDialogFragment.TAG)
        }
    }
}