package com.stopstone.musicplaylist.ui.my

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.data.model.entity.SignatureSong
import com.stopstone.musicplaylist.databinding.FragmentMyBinding
import com.stopstone.musicplaylist.databinding.ViewMySettingBinding
import com.stopstone.musicplaylist.notification.NotificationScheduler
import com.stopstone.musicplaylist.ui.common.SignatureSongTextFormatter
import com.stopstone.musicplaylist.ui.my.model.MySettingMenu
import com.stopstone.musicplaylist.ui.my.viewmodel.MyViewModel
import com.stopstone.musicplaylist.ui.signature_list.SignatureListActivity
import com.stopstone.musicplaylist.util.DateUtils
import com.stopstone.musicplaylist.util.loadImage
import com.stopstone.musicplaylist.util.setOnCheckedChangeWithHaptic
import com.stopstone.musicplaylist.util.setOnClickWithHaptic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFragment : Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private val viewModel: MyViewModel by viewModels()
    private var isNotificationSwitchListenerEnabled: Boolean = true
    private var pendingEnableRequest: Boolean = false

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                pendingEnableRequest = false
                enableDailyReminder()
                updateNotificationSwitchState(true)
            } else {
                pendingEnableRequest = false
                updateNotificationSwitchState(false)
                if (!shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionDeniedDialog()
                }
            }
        }

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
        setupListeners()
        setupNotificationSetting()
        observeViewModel()
        viewModel.loadMusicCount()
        viewModel.loadSignatureSong()
    }

    private fun setupListeners() {
        with(binding) {
            mySignatureSongNone.cardMySignatureSong.setOnClickWithHaptic {
                navigateToMusicSearchForSignatureSong()
            }

            mySignatureSong.cardMySignatureSong.setOnClickWithHaptic {
                navigateToMusicSearchForSignatureSong()
            }

            mySignatureSong.signatureSongMore.setOnClickListener {
                navigateToSignatureSongList()
            }
        }
    }

    private fun setupNotificationSetting() {
        binding.layoutAlarmRow.setOnClickListener {
            binding.switchShowEmotions.performClick()
        }

        binding.switchShowEmotions.setOnCheckedChangeWithHaptic { isChecked ->
            if (!isNotificationSwitchListenerEnabled) {
                return@setOnCheckedChangeWithHaptic
            }
            handleNotificationToggle(isChecked)
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
                        updateNotificationSwitchState(uiState.isDailyReminderEnabled)
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
        if (count == 0) {
            // 음악 기록이 없을 때 문구
            val headerText = getString(R.string.label_my_music_count_empty)
            binding.tvMyHeader.text = headerText
        } else {
            val fullText = getString(R.string.label_my_music_count, count)
            val parts = fullText.split(" ")
            if (parts.isNotEmpty()) {
                val lastPart = parts.last() // 마지막 부분 (예: "5곡")
                val spannableString = SpannableString(fullText)
                val startIndex = fullText.lastIndexOf(lastPart)
                val endIndex = startIndex + lastPart.length

                // 색상 변경
                val color = ContextCompat.getColor(requireContext(), R.color.music_count_highlight)
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    startIndex,
                    endIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                binding.tvMyHeader.text = spannableString
            } else {
                binding.tvMyHeader.text = fullText
            }
        }
    }

    private fun updateSignatureSongView(signatureSong: SignatureSong?) {
        if (signatureSong != null) {
            // 인생곡이 있을 때
            binding.mySignatureSongNone.root.visibility = View.INVISIBLE
            binding.mySignatureSong.root.visibility = View.VISIBLE

            // 인생곡 정보 표시
            with(binding.mySignatureSong) {
                ivSignatureSong.loadImage(signatureSong.track.imageUrl)

                tvSignatureSongTitle.text = signatureSong.track.title
                tvSignatureSongArtist.text = signatureSong.track.artist
                val daysSinceSelected = DateUtils.getDaysSince(signatureSong.selectedAt)
                tvSignatureSongSince.text =
                    SignatureSongTextFormatter.buildDaysOnlyText(
                        context = context,
                        daysSinceSelected = daysSinceSelected,
                    )
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

    private fun handleNotificationToggle(shouldEnable: Boolean) {
        if (shouldEnable) {
            if (hasNotificationPermission()) {
                pendingEnableRequest = false
                enableDailyReminder()
                updateNotificationSwitchState(true)
            } else {
                pendingEnableRequest = true
                updateNotificationSwitchState(false)
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            pendingEnableRequest = false
            disableDailyReminder()
            updateNotificationSwitchState(false)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun enableDailyReminder() {
        NotificationScheduler.scheduleDailyMusicReminder(requireContext().applicationContext)
        viewModel.updateDailyReminderEnabled(true)
    }

    private fun disableDailyReminder() {
        NotificationScheduler.cancelDailyNotification(requireContext().applicationContext)
        viewModel.updateDailyReminderEnabled(false)
    }

    private fun updateNotificationSwitchState(isEnabled: Boolean) {
        if (binding.switchShowEmotions.isChecked == isEnabled) {
            return
        }
        isNotificationSwitchListenerEnabled = false
        binding.switchShowEmotions.isChecked = isEnabled
        isNotificationSwitchListenerEnabled = true
    }

    private fun showNotificationPermissionDeniedDialog() {
        AlertDialog
            .Builder(requireContext())
            .setTitle(R.string.label_notification_setting_title)
            .setMessage(R.string.message_notification_permission_required)
            .setPositiveButton(R.string.label_open_notification_settings) { _, _ ->
                openNotificationSettings()
            }.setNegativeButton(R.string.label_common_cancel, null)
            .show()
    }

    private fun openNotificationSettings() {
        val intent =
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
            }
        startActivity(intent)
    }
}
