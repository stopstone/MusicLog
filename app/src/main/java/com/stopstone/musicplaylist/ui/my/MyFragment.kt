package com.stopstone.musicplaylist.ui.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.FragmentMyBinding
import com.stopstone.musicplaylist.databinding.ViewMySettingBinding
import com.stopstone.musicplaylist.ui.my.model.MySettingMenu
import com.stopstone.musicplaylist.ui.my.viewmodel.MyViewModel
import com.stopstone.musicplaylist.ui.signature_list.SignatureListActivity
import com.stopstone.musicplaylist.util.DateUtils
import com.stopstone.musicplaylist.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private val viewModel: MyViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupSettingItems()
        setupSignatureSongClick()
        observeViewModel()
        viewModel.loadMusicCount()
        viewModel.loadSignatureSong()
    }

    private fun setupSignatureSongClick() {
        binding.mySignatureSongNone.cardMySignatureSong.setOnClickListener {
            navigateToMusicSearchForSignatureSong()
        }
        binding.mySignatureSong.signatureSongMore.setOnClickListener {
            navigateToSignatureSongList()
        }
    }

    private fun navigateToMusicSearchForSignatureSong() {
        val action = MyFragmentDirections.actionMyToMusicSearch("SIGNATURE_SONG")
        findNavController().navigate(action)
    }

    private fun navigateToSignatureSongList() {
        val intent = Intent(requireContext(), SignatureListActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupSettingItems() {
        MySettingMenu.entries.forEach { menu ->
            createSettingView(menu)
        }
    }

    private fun createSettingView(menu: MySettingMenu) {
        ViewMySettingBinding
            .inflate(
                layoutInflater,
                binding.llSettingsContainer,
                false,
            ).apply {
                tvSettingTitle.setText(menu.titleRes)
                root.setOnClickListener {
                    actionSettingMenu(menu)
                }
                // 컨테이너에 추가
                binding.llSettingsContainer.addView(root)
            }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { uiState ->
                        updateMusicCount(uiState.musicCount)
                    }
                }

                launch {
                    viewModel.signatureSong.collect { signatureSong ->
                        updateSignatureSongView(signatureSong)
                    }
                }
            }
        }
    }

    private fun updateMusicCount(count: Int) {
        binding.tvMyHeader.text = getString(R.string.label_my_music_count, count)
    }

    private fun updateSignatureSongView(signatureSong: com.stopstone.musicplaylist.data.model.entity.SignatureSong?) {
        if (signatureSong != null) {
            // 인생곡이 있을 때
            binding.mySignatureSongNone.root.visibility = View.INVISIBLE
            binding.mySignatureSong.root.visibility = View.VISIBLE

            // 인생곡 정보 표시
            with(binding.mySignatureSong) {
                ivSignatureSong.loadImage(signatureSong.track.imageUrl)

                tvSignatureSongTitle.text = signatureSong.track.title
                tvSignatureSongArtist.text = signatureSong.track.artist
                tvSignatureSongSince.text = DateUtils.formatSignatureSongDate(signatureSong.selectedAt)
            }
        } else {
            // 인생곡이 없을 때
            binding.mySignatureSongNone.root.visibility = View.VISIBLE
            binding.mySignatureSong.root.visibility = View.INVISIBLE
        }
    }

    private fun actionSettingMenu(menu: MySettingMenu) {
        findNavController().navigate(menu.destinationId)
    }
}
