package com.stopstone.musicplaylist.ui.my

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stopstone.musicplaylist.databinding.FragmentMyBinding
import com.stopstone.musicplaylist.databinding.ViewMySettingBinding
import com.stopstone.musicplaylist.ui.my.model.MySettingMenu

class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context

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

    private fun actionSettingMenu(menu: MySettingMenu) {
        // TODO: Navigation Component 구현 후 아래 주석 해제
        // findNavController().navigate(menu.destinationId)
    }
}
