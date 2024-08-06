package com.stopstone.myapplication.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stopstone.myapplication.databinding.FragmentTrackConfirmDialogBinding
import com.stopstone.myapplication.ui.search.viewmodel.TrackViewModel
import com.stopstone.myapplication.util.loadImage
import com.stopstone.myapplication.util.showToast
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
        observeViewModel()
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
            dismiss()
        }
        binding.btnTrackCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trackSaved.collectLatest { trackSaved ->
                    when (trackSaved) {
                        true -> requireContext().showToast("저장에 성공했습니다.")
                        false -> requireContext().showToast("저장에 실패했습니다.")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}