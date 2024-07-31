package com.stopstone.myapplication.ui.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.myapplication.R
import com.stopstone.myapplication.data.model.SearchHistory
import com.stopstone.myapplication.databinding.FragmentSearchBinding
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.ui.adapter.OnItemClickListener
import com.stopstone.myapplication.ui.adapter.SearchHistoryAdapter
import com.stopstone.myapplication.ui.adapter.TrackAdapter
import com.stopstone.myapplication.ui.viewmodel.SearchState
import com.stopstone.myapplication.ui.viewmodel.SearchViewModel
import com.stopstone.myapplication.util.hideKeyboard
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
        when(item) {
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
            if (searches.isEmpty()) {
                binding.groupRecentSearches.visibility = View.GONE
            } else {
                binding.groupRecentSearches.visibility = View.VISIBLE
            }
            searchHistoryAdapter.submitList(searches)

        }
    }

    private suspend fun observeSearchState() {
        viewModel.searchState.collectLatest { state ->
            when (state) {
                is SearchState.Initial -> {
                    viewModel.loadSearchHistory()
                }

                is SearchState.Success -> state.tracks.also { tracks ->
                    binding.groupRecentSearches.isVisible = false
                    binding.layoutTracksEmpty.root.isVisible = tracks.isEmpty()
                    binding.rvSearchTrackList.isVisible = tracks.isNotEmpty()
                    trackAdapter.submitList(tracks)
                }

                is SearchState.Error -> {
                    showToastMessage(state.message)
                }
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

            false -> showToastMessage(getString(R.string.search_empty_message))
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

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}