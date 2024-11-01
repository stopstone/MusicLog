package com.stopstone.musicplaylist.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.databinding.FragmentSearchBinding
import com.stopstone.musicplaylist.ui.common.adapter.TrackAdapter
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.ui.search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.ui.search.adapter.SearchHistoryAdapter
import com.stopstone.musicplaylist.ui.search.viewmodel.SearchViewModel
import com.stopstone.musicplaylist.util.hideKeyboard
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val trackAdapter: TrackAdapter by lazy { TrackAdapter(this) }
    private val searchHistoryAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(this) }
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
        observeViewModel()
        setRecyclerView()
        setListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is SearchHistory -> {
                binding.etSearchTrack.setText(item.query).toString()
                searchTracks()
                viewModel.addSearch(item.query)
            }

            is TrackUiState -> {
                val action = SearchFragmentDirections.actionSearchToTrackConfirmDialog(item)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDeleteClick(search: SearchHistory) {
        viewModel.deleteSearch(search)
    }

    private fun setRecyclerView() {
        binding.rvSearchTrackList.adapter = trackAdapter
        binding.rvSearchHistoryList.adapter = searchHistoryAdapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeSearchState() }
                launch { observeSearchHistory() }
            }
        }
    }

    private suspend fun observeSearchHistory() {
        viewModel.searchHistory.collect { searches ->
            binding.groupRecentSearches.visibility = if (searches.isEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
            searchHistoryAdapter.submitList(searches)
        }
    }

    private suspend fun observeSearchState() {
        viewModel.searchList.collectLatest { tracks ->
            if (tracks.isNotEmpty()) {
                binding.groupRecentSearches.isVisible = false
                binding.layoutTracksEmpty.root.isVisible = tracks.isEmpty()
                binding.rvSearchTrackList.isVisible = tracks.isNotEmpty()
                trackAdapter.submitList(tracks)
            } else {
                viewModel.loadSearchHistory()
            }
        }
    }

    private fun searchTracks() {
        val track = binding.etSearchTrack.text.toString().trim()
        when (track.isNotEmpty()) {
            true -> {
                viewModel.searchTracks(track)
                viewModel.addSearch(track)
            }

            false -> requireContext().showToast(getString(R.string.search_empty_message))
        }
        view?.hideKeyboard()
    }

    private fun setListeners() {
        binding.btnSearchTrack.setOnClickListener {
            searchTracks()
        }

        binding.etSearchTrack.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    searchTracks()
                    true
                }

                else -> false
            }
        }

        binding.etSearchTrack.doAfterTextChanged { text ->
            if (text.isNullOrEmpty()) {
                binding.groupRecentSearches.visibility = View.VISIBLE
                binding.btnCancelSearch.visibility = View.GONE
                binding.rvSearchTrackList.visibility = View.GONE
            } else {
                binding.groupRecentSearches.visibility = View.GONE
                binding.btnCancelSearch.visibility = View.VISIBLE
            }
        }

        binding.btnCancelSearch.setOnClickListener {
            binding.etSearchTrack.text.clear()
        }

        binding.tvClearAll.setOnClickListener {
            viewModel.clearAllSearches()
        }
    }
}