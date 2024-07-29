package com.stopstone.myapplication.ui.view.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.FragmentSearchBinding
import com.stopstone.myapplication.ui.adapter.TrackAdapter
import com.stopstone.myapplication.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter: TrackAdapter by lazy { TrackAdapter() }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearchTrackList.adapter = adapter
        setListeners()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeTracks() }
            }
        }

        binding.etSearchTrack.doAfterTextChanged { text ->
            binding.btnCancelSearch.visibility = if (text.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun searchTracks() {
        val track = binding.etSearchTrack.text.toString()
        when (track.isNotEmpty()) {
            true -> viewModel.searchTracks(track)
            false -> showToastMessage(getString(R.string.search_empty_message))
        }
        hideKeyboard()
    }

    private suspend fun observeTracks() {
        viewModel.tracksUiState.collectLatest { trackList ->
            if (trackList.isEmpty()) {
                binding.layoutTracksEmpty.root.visibility = View.VISIBLE
            } else {
                binding.layoutTracksEmpty.root.visibility = View.GONE
                adapter.submitList(trackList)
            }
        }
    }

    private fun setListeners() {
        adapter.setOnItemClickListener { track ->
            val action = SearchFragmentDirections.actionSearchToTrackConfirmDialog(track)
            findNavController().navigate(action)
        }

        binding.btnSearchTrack.setOnClickListener {
            searchTracks()
        }

        binding.etSearchTrack.setOnEditorActionListener { v, actionId, event ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    searchTracks()
                    true
                }

                else -> false
            }
        }

        binding.btnCancelSearch.setOnClickListener {
            binding.etSearchTrack.text.clear()
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}