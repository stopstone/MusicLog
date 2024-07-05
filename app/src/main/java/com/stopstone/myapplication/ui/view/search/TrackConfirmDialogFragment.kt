package com.stopstone.myapplication.ui.view.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.stopstone.myapplication.data.model.SaveResult
import com.stopstone.myapplication.databinding.FragmentTrackConfirmDialogBinding
import com.stopstone.myapplication.ui.viewmodel.TrackViewModel
import dagger.hilt.android.AndroidEntryPoint

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
            Glide.with(binding.root)
                .load(track.album.images.firstOrNull()?.url)
                .centerCrop()
                .into(ivConfirmTrackImage)
            tvConfirmTrackInfo.text = "${track.artists.joinToString(", ") { it.name }} - ${track.name}"
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

    private fun observeViewModel() {
        viewModel.savedTrack.observe(viewLifecycleOwner) { result ->
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
