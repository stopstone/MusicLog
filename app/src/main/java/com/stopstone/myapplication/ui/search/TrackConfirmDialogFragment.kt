package com.stopstone.myapplication.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.stopstone.myapplication.data.model.Emotions
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
            ivConfirmTrackImage.loadImage(track.imageUrl)
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
                launch { collectSaveTrack() }
                launch { setupEmotionChips() }
                launch { collectSelectEmotions() }
            }
        }
    }

    private suspend fun collectSaveTrack() {
        viewModel.trackSaved.collectLatest { trackSaved ->
            when (trackSaved) {
                true -> requireContext().showToast("저장에 성공했습니다.")
                false -> requireContext().showToast("저장에 실패했습니다.")
            }
        }
    }

    private suspend fun collectSelectEmotions() {
        viewModel.selectedEmotions.collectLatest { selectedEmotions ->
            updateChipSelection(selectedEmotions)
        }
    }

    private suspend fun setupEmotionChips() {
        viewModel.emotions.collectLatest { emotions ->
            binding.chipGroupConfirmTrackEmotion.removeAllViews()
            emotions.forEach { emotion ->
                val chip = Chip(requireContext()).apply {
                    text = emotion.displayName
                    isCheckable = true
                    setOnCheckedChangeListener { _, _ ->
                        viewModel.toggleEmotion(emotion)
                    }
                }
                binding.chipGroupConfirmTrackEmotion.addView(chip)
            }
        }
    }

    private fun updateChipSelection(selectedEmotions: List<Emotions>) {
        binding.chipGroupConfirmTrackEmotion.children.forEach { child ->
            if (child is Chip) {
                child.isChecked = selectedEmotions.any { it.displayName == child.text }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}