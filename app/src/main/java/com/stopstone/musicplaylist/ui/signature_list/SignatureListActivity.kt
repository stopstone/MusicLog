package com.stopstone.musicplaylist.ui.signature_list

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.stopstone.musicplaylist.databinding.ActivitySignatureListBinding
import com.stopstone.musicplaylist.ui.signature_list.adapter.SignatureSongHistoryAdapter
import com.stopstone.musicplaylist.ui.signature_list.model.SignatureListUiState
import com.stopstone.musicplaylist.ui.signature_list.viewmodel.SignatureListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity that renders the signature song history list.
 */
@AndroidEntryPoint
class SignatureListActivity : AppCompatActivity() {
    private val binding: ActivitySignatureListBinding by lazy {
        ActivitySignatureListBinding.inflate(layoutInflater)
    }
    private val viewModel: SignatureListViewModel by viewModels()
    private val signatureSongAdapter = SignatureSongHistoryAdapter()
    private var lastErrorMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setupToolbar()
        setupRecyclerView()
        observeUiState()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.layoutSignatureListRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupToolbar() {
        binding.toolbarSignatureSongList.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvSignatureMusicList.apply {
            layoutManager = LinearLayoutManager(this@SignatureListActivity)
            adapter = signatureSongAdapter
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: SignatureListUiState) {
        binding.piSignatureListLoading.isVisible = state.isLoading
        val hasSongs = state.songs.isNotEmpty()
        binding.rvSignatureMusicList.isVisible = hasSongs
        binding.tvSignatureListEmpty.isVisible = !state.isLoading && !hasSongs
        signatureSongAdapter.submitList(state.songs)
        showErrorIfNeeded(state.errorMessage)
    }

    private fun showErrorIfNeeded(message: String?) {
        if (!message.isNullOrBlank() && message != lastErrorMessage) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            lastErrorMessage = message
        }
    }
}
