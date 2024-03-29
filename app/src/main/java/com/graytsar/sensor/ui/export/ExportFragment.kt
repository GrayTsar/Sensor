package com.graytsar.sensor.ui.export

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.databinding.FragmentExportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A Fragment that displays a list of recording sessions.
 * A user can select a session to export.
 * A user can select a session to delete.
 */
@AndroidEntryPoint
class ExportFragment : Fragment() {
    private val viewModel: ExportViewModel by viewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: AdapterExport

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentExportBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController
        )

        adapter = AdapterExport(this, viewModel)
        recycler = binding.recyclerExport
        recycler.adapter = adapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pager.collectLatest {
                adapter.submitData(it)
            }
        }
    }
}