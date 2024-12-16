package com.stopstone.musicplaylist.ui.search

import android.content.Context
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
import com.stopstone.musicplaylist.ui.search.viewmodel.SearchUiState
import com.stopstone.musicplaylist.ui.search.viewmodel.SearchViewModel
import com.stopstone.musicplaylist.util.hideKeyboard
import com.stopstone.musicplaylist.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val trackAdapter: TrackAdapter by lazy { TrackAdapter(this) }
    private val searchHistoryAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(this) }
    private val viewModel: SearchViewModel by viewModels()

    private lateinit var appContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is SearchHistory -> {
                val query = item.query
                viewModel.updateQuery(query)
                binding.etSearchTrack.editText?.setText(query)
                binding.etSearchTrack.editText?.setSelection(query.length)
                performSearch()
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

    private fun setupViews() {
        binding.rvSearchTrackList.adapter = trackAdapter
        binding.rvSearchHistoryList.adapter = searchHistoryAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        updateUiState(state)
                    }
                }
                launch {
                    viewModel.searchHistory.collect { searches ->
                        searchHistoryAdapter.submitList(searches)

                        if (viewModel.uiState.value !is SearchUiState.Success &&
                            viewModel.uiState.value !is SearchUiState.Loading) {
                            if (viewModel.uiState.value is SearchUiState.Initial ||
                                viewModel.uiState.value is SearchUiState.ShowHistory) {
                                binding.groupRecentSearches.isVisible = searches.isNotEmpty()
                                binding.layoutTracksEmpty.apply {
                                    root.isVisible = searches.isEmpty()
                                    tvTrackEmptySubtitle.text = getString(R.string.label_history_empty_subtitle)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.query.collect { query ->
                        val editText = binding.etSearchTrack.editText
                        if (editText?.text.toString() != query) {
                            editText?.setText(query)
                            editText?.setSelection(query.length)
                        }
                    }
                }
            }
        }
    }

    private fun updateUiState(state: SearchUiState) {
        binding.progressBar.isVisible = state is SearchUiState.Loading

        when (state) {
            is SearchUiState.Initial,
            is SearchUiState.ShowHistory -> {
                binding.groupRecentSearches.isVisible =
                    viewModel.searchHistory.value.isNotEmpty()
                binding.layoutTracksEmpty.root.isVisible =
                    viewModel.searchHistory.value.isEmpty()
                binding.rvSearchTrackList.isVisible = false
            }
            is SearchUiState.Success -> {
                binding.layoutTracksEmpty.root.isVisible = false
                binding.groupRecentSearches.isVisible = false
                binding.rvSearchTrackList.isVisible = true
                trackAdapter.submitList(state.tracks)
            }
            is SearchUiState.Empty -> {
                binding.layoutTracksEmpty.apply {
                    root.isVisible = true
                    tvTrackEmptySubtitle.text = getString(R.string.label_track_empty_subtitle)
                }
                binding.groupRecentSearches.isVisible = false
                binding.rvSearchTrackList.isVisible = false
            }
            is SearchUiState.Loading -> {
                binding.layoutTracksEmpty.root.isVisible = false
                binding.groupRecentSearches.isVisible = false
                binding.rvSearchTrackList.isVisible = false
            }
            else -> {}
        }
    }

    private fun setListeners() {
        binding.apply {
            btnSearchTrack.setOnClickListener {
                performSearch()
                etSearchTrack.clearFocus()
            }

            etSearchTrack.editText?.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        performSearch()
                        true
                    }
                    else -> false
                }
            }

            etSearchTrack.editText?.doAfterTextChanged { text ->
                viewModel.updateQuery(text?.toString() ?: "")
                if (text.isNullOrEmpty()) {
                    viewModel.resetToHistory()
                }
            }

            tvClearAll.setOnClickListener {
                viewModel.clearAllSearches()
            }

            root.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view?.hideKeyboard()
                }
                false
            }
        }
    }

    private fun performSearch() {
        val query = binding.etSearchTrack.editText?.text.toString().trim()
        if (query.isEmpty()) {
            appContext.showToast(getString(R.string.search_empty_message))
            return
        }

        viewModel.searchTracks(query)
        view?.hideKeyboard()
    }

}