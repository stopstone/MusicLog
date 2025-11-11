package com.stopstone.musicplaylist.ui.my

import android.content.Context
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
import com.stopstone.musicplaylist.databinding.FragmentMyBinding
import com.stopstone.musicplaylist.databinding.ViewMySettingBinding
import com.stopstone.musicplaylist.ui.my.model.MySettingMenu
import com.stopstone.musicplaylist.ui.my.viewmodel.MyViewModel
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
        observeViewModel()
        viewModel.loadMusicCount()
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
                tvSettingTitle.text = menu.title
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
                viewModel.uiState.collect { uiState ->
                    updateMusicCount(uiState.musicCount)
                }
            }
        }
    }

    private fun updateMusicCount(count: Int) {
        binding.tvMyHeader.text = "지금까지 기록한 감정플리\n${count}곡"
    }

    private fun actionSettingMenu(menu: MySettingMenu) {
        findNavController().navigate(menu.destinationId)
    }
}
