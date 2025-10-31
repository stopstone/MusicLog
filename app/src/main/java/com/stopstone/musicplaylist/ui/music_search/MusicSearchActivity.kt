package com.stopstone.musicplaylist.ui.music_search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.databinding.ActivityMusicSearchBinding
import com.stopstone.musicplaylist.ui.common.adapter.TrackAdapter
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.ui.music_memo.MusicMemoActivity
import com.stopstone.musicplaylist.ui.music_search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.ui.music_search.adapter.SearchHistoryAdapter
import com.stopstone.musicplaylist.ui.music_search.viewmodel.SearchUiState
import com.stopstone.musicplaylist.ui.music_search.viewmodel.SearchViewModel
import com.stopstone.musicplaylist.util.hideKeyboard
import com.stopstone.musicplaylist.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicSearchActivity :
    AppCompatActivity(),
    OnItemClickListener {
    private val binding: ActivityMusicSearchBinding by lazy {
        ActivityMusicSearchBinding.inflate(
            layoutInflater,
        )
    }
    private val trackAdapter: TrackAdapter by lazy { TrackAdapter(this) }
    private val historyAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter(this) }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWindowInsets()
        setContentView(binding.root)
        setupRecycler()
        setupListeners()
        setupObservers()
        focusSearchField()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        when (state) {
                            is SearchUiState.Initial -> {}
                            is SearchUiState.ShowHistory -> {
                                binding.pbLoading.visibility = View.GONE
                                binding.tvEmpty.visibility = View.GONE
                                binding.rvSearchResult.visibility = View.VISIBLE
                                binding.rvSearchResult.adapter = historyAdapter
                                launch {
                                    viewModel.searchHistory.collect {
                                        historyAdapter.submitList(
                                            it,
                                        )
                                    }
                                }
                            }

                            is SearchUiState.Loading -> {
                                binding.pbLoading.visibility = View.VISIBLE
                                binding.tvEmpty.visibility = View.GONE
                                binding.rvSearchResult.visibility = View.GONE
                            }

                            is SearchUiState.Success -> {
                                binding.pbLoading.visibility = View.GONE
                                binding.tvEmpty.visibility =
                                    if (state.tracks.isEmpty()) View.VISIBLE else View.GONE
                                binding.rvSearchResult.visibility =
                                    if (state.tracks.isEmpty()) View.GONE else View.VISIBLE
                                binding.rvSearchResult.adapter = trackAdapter
                                trackAdapter.submitList(state.tracks)
                            }

                            is SearchUiState.Empty -> {
                                binding.pbLoading.visibility = View.GONE
                                binding.tvEmpty.visibility = View.VISIBLE
                                binding.rvSearchResult.visibility = View.GONE
                                binding.rvSearchResult.adapter = trackAdapter
                                trackAdapter.submitList(emptyList())
                            }

                            is SearchUiState.Error -> {
                                binding.pbLoading.visibility = View.GONE
                                binding.tvEmpty.visibility = View.VISIBLE
                                binding.rvSearchResult.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupRecycler() {
        binding.rvSearchResult.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResult.adapter = historyAdapter
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is SearchHistory -> {
                val query: String = item.query
                if (query.isNotEmpty()) {
                    binding.etSearch.setText(query)
                    binding.etSearch.setSelection(query.length)
                    binding.etSearch.showKeyboard()
                    viewModel.updateQuery(query)
                    viewModel.searchTracks(query)
                }
            }

            is TrackUiState -> {
                navigateToMusicMemo(item)
            }

            else -> {
            }
        }
        binding.etSearch.hideKeyboard()
    }

    override fun onDeleteClick(search: SearchHistory) {
        viewModel.deleteSearch(search)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupListeners() {
        with(binding) {
            searchToolbar.setNavigationOnClickListener {
                finish()
            }

            // 키보드 검색 버튼 클릭
            etSearch.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query: String =
                        textView.text
                            ?.toString()
                            ?.trim()
                            .orEmpty()
                    if (query.isNotEmpty()) {
                        viewModel.updateQuery(query)
                        viewModel.searchTracks(query)
                    }
                    etSearch.hideKeyboard()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun focusSearchField() {
        binding.etSearch.showKeyboard()
    }

    private fun navigateToMusicMemo(track: TrackUiState) {
        val intent = MusicMemoActivity.createIntent(this, track)
        startActivity(intent)
    }
}
