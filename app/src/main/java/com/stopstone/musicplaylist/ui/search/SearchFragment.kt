package com.stopstone.musicplaylist.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
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
                binding.etSearchTrack.editText?.setText(item.query).toString()
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

    private suspend fun observeSearchState() {
        viewModel.searchList.collectLatest { tracks ->
            val isSearching = !binding.etSearchTrack.editText?.text.isNullOrEmpty()

            when {
                isSearching -> {
                    // 검색 중일 때
                    binding.groupRecentSearches.isVisible = false
                    binding.layoutTracksEmpty.root.isVisible = tracks.isEmpty()
                    binding.rvSearchTrackList.isVisible = tracks.isNotEmpty()
                    trackAdapter.submitList(tracks)
                }
                else -> {
                    // 검색어가 없을 때
                    viewModel.loadSearchHistory()
                    binding.rvSearchTrackList.isVisible = false
                    binding.layoutTracksEmpty.root.isVisible = true
                    binding.groupRecentSearches.isVisible = false
                }
            }
        }
    }

    private suspend fun observeSearchHistory() {
        viewModel.searchHistory.collect { searches ->
            binding.groupRecentSearches.isVisible = binding.etSearchTrack.editText?.text.toString().isEmpty()
            binding.layoutTracksEmpty.root.isVisible = searches.isEmpty() && binding.etSearchTrack.editText?.text.toString().isEmpty()
            searchHistoryAdapter.submitList(searches)
        }
    }

    private fun searchTracks() {
        val track = binding.etSearchTrack.editText?.text.toString().trim()
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

        binding.etSearchTrack.editText?.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    searchTracks()
                    true
                }

                else -> false
            }
        }

        binding.etSearchTrack.editText?.doAfterTextChanged { text ->
            when(text.isNullOrEmpty()) {
                true -> {
                    binding.groupRecentSearches.visibility = View.VISIBLE
                    binding.rvSearchTrackList.visibility = View.GONE
                }
                false -> {
                    binding.groupRecentSearches.visibility = View.GONE
                }
            }
        }

        binding.tvClearAll.setOnClickListener {
            viewModel.clearAllSearches()
        }

        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                view?.hideKeyboard()
            }
            true
        }

    }
}