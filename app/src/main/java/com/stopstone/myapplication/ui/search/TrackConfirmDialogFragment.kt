package com.stopstone.myapplication.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stopstone.myapplication.domain.model.SaveResult
import com.stopstone.myapplication.databinding.FragmentTrackConfirmDialogBinding
import com.stopstone.myapplication.ui.search.viewmodel.TrackViewModel
import com.stopstone.myapplication.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrackConfirmDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentTrackConfirmDialogBinding? = null
    private val binding get() = _binding!!
    private val args: TrackConfirmDialogFragmentArgs by navArgs()
    private val viewModel: TrackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackConfirmDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
        setListeners()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { observeViewModel() }
            }
        }
    }

    private fun setLayout() {
        val track = args.track
        with(binding) {
            track.imageUrl?.let { ivConfirmTrackImage.loadImage(it) }
            tvConfirmTrackInfo.text = "${track.artist} - ${track.title}"
        }
    }

    private fun setListeners() {
        binding.btnTrackConfirm.setOnClickListener {
            viewModel.saveTrack(args.track)
        }
        binding.btnTrackCancel.setOnClickListener {
            dismiss()
        }
    }

    private suspend fun observeViewModel() {
        viewModel.savedTrack.collectLatest { result ->
            when (result) {
                is SaveResult.Success -> {
                    Toast.makeText(context, "트랙이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
                is SaveResult.Error -> {
                    Log.e("TrackConfirmDialogFragment", "저장에 실패했습니다: ${result.message}")
                }
            }
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
