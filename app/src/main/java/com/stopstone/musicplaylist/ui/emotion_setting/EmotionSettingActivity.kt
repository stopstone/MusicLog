package com.stopstone.musicplaylist.ui.emotion_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityEmotionSettingBinding
import com.stopstone.musicplaylist.ui.emotion_setting.adapter.EmotionAdapter
import com.stopstone.musicplaylist.ui.emotion_setting.adapter.EmotionClickListener
import com.stopstone.musicplaylist.ui.emotion_setting.helper.EmotionItemTouchHelper
import com.stopstone.musicplaylist.ui.emotion_setting.helper.ItemTouchHelperAdapter
import com.stopstone.musicplaylist.ui.emotion_setting.model.EmotionUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmotionSettingActivity :
    AppCompatActivity(),
    EmotionClickListener,
    ItemTouchHelperAdapter {
    private val binding: ActivityEmotionSettingBinding by lazy {
        ActivityEmotionSettingBinding.inflate(LayoutInflater.from(this))
    }

    private val viewModel: EmotionSettingViewModel by viewModels()

    private val emotionAdapter: EmotionAdapter by lazy {
        EmotionAdapter(this)
    }

    private lateinit var itemTouchHelper: ItemTouchHelper
    private var isFabMenuOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupLayout()
        setupItemTouchHelper()
        setupBackPress()
        observeViewModel()
    }

    private fun setupLayout() {
        with(binding) {
            // Toolbar 뒤로가기
            toolbarEmotionSetting.setNavigationOnClickListener {
                finish()
            }

            // RecyclerView 설정
            rvEmotions.adapter = emotionAdapter

            // 메인 FAB 클릭
            fabMain.setOnClickListener {
                toggleFabMenu()
            }

            // 배경 클릭 시 메뉴 닫기
            viewDimBackground.setOnClickListener {
                closeFabMenu()
            }

            // 추가 메뉴 클릭
            tvAddEmotion.setOnClickListener {
                closeFabMenu()
                showAddEmotionDialog()
            }

            // 삭제 메뉴 클릭
            tvDeleteEmotion.setOnClickListener {
                closeFabMenu()
                enterDeleteMode()
            }

            // 하단 삭제 버튼 클릭
            btnDeleteSelected.setOnClickListener {
                showDeleteConfirmDialog()
            }
        }
    }

    private fun setupItemTouchHelper() {
        val callback = EmotionItemTouchHelper(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvEmotions)
    }

    private fun setupBackPress() {
        val callback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        isFabMenuOpen -> closeFabMenu()
                        viewModel.isDeleteMode.value -> exitDeleteMode()
                        else -> {
                            isEnabled = false
                            onBackPressedDispatcher.onBackPressed()
                        }
                    }
                }
            }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.emotions.collect { emotions ->
                        emotionAdapter.submitList(emotions)
                    }
                }

                launch {
                    viewModel.isDeleteMode.collect { isDeleteMode ->
                        emotionAdapter.setDeleteMode(isDeleteMode)
                        updateDeleteModeUI(isDeleteMode)
                    }
                }

                launch {
                    viewModel.selectedCount.collect { count ->
                        updateDeleteButtonText(count)
                    }
                }
            }
        }
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabMenuOpen = true
        binding.fabMain
            .animate()
            .rotation(45f)
            .setDuration(200)
            .start()

        // 배경 어둡게
        binding.viewDimBackground.apply {
            visibility = View.VISIBLE
            isClickable = true
            animate().alpha(1f).setDuration(200).start()
        }

        // 메뉴 카드 애니메이션
        binding.cardEmotionMenu.apply {
            visibility = View.VISIBLE
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(250)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun closeFabMenu() {
        isFabMenuOpen = false
        binding.fabMain
            .animate()
            .rotation(0f)
            .setDuration(200)
            .start()

        // 배경 투명하게
        binding.viewDimBackground
            .animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                binding.viewDimBackground.apply {
                    visibility = View.GONE
                    isClickable = false
                }
            }.start()

        // 메뉴 카드 애니메이션
        binding.cardEmotionMenu
            .animate()
            .translationY(100f)
            .alpha(0f)
            .setDuration(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                binding.cardEmotionMenu.visibility = View.GONE
            }.start()
    }

    private fun showAddEmotionDialog() {
        val editText =
            EditText(this).apply {
                hint = getString(R.string.hint_emotion_name)
                setPadding(48, 32, 48, 32)
            }

        AlertDialog
            .Builder(this)
            .setTitle(R.string.label_add_custom_emotion)
            .setView(editText)
            .setPositiveButton(R.string.label_confirm) { _, _ ->
                val emotionName = editText.text.toString().trim()
                if (emotionName.isEmpty()) {
                    showSnackbar(getString(R.string.message_emotion_name_empty))
                } else {
                    val success = viewModel.addCustomEmotion(emotionName)
                    if (!success) {
                        showSnackbar(getString(R.string.message_emotion_already_exists))
                    }
                }
            }.setNegativeButton(R.string.label_cancel, null)
            .show()
    }

    private fun enterDeleteMode() {
        viewModel.enableDeleteMode()
    }

    private fun exitDeleteMode() {
        viewModel.disableDeleteMode()
    }

    private fun updateDeleteModeUI(isDeleteMode: Boolean) {
        with(binding) {
            if (isDeleteMode) {
                // 삭제 모드: FAB 숨기고 삭제 버튼 표시
                fabMain.visibility = View.GONE
                btnDeleteSelected.visibility = View.VISIBLE
                // 드래그 비활성화
                itemTouchHelper.attachToRecyclerView(null)
            } else {
                // 일반 모드: 삭제 버튼 숨기고 FAB 표시
                btnDeleteSelected.visibility = View.GONE
                fabMain.visibility = View.VISIBLE
                // 드래그 활성화
                itemTouchHelper.attachToRecyclerView(rvEmotions)
            }
        }
    }

    private fun updateDeleteButtonText(count: Int) {
        with(binding.btnDeleteSelected) {
            isEnabled = count > 0
            text =
                if (count > 0) {
                    getString(R.string.label_delete_n_emotions, count)
                } else {
                    getString(R.string.label_delete_selected_emotions)
                }
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog
            .Builder(this)
            .setTitle(R.string.message_delete_confirm_title)
            .setMessage(R.string.message_delete_confirm)
            .setPositiveButton(R.string.label_delete) { _, _ ->
                viewModel.deleteSelectedEmotions()
                showSnackbar(getString(R.string.message_emotion_deleted))
            }.setNegativeButton(R.string.label_cancel, null)
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    // EmotionClickListener 구현
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    override fun onEmotionClick(emotion: EmotionUiState) {
        if (viewModel.isDeleteMode.value) {
            viewModel.toggleEmotionSelection(emotion)
        }
    }

    // ItemTouchHelperAdapter 구현
    override fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    ) {
        emotionAdapter.moveItem(fromPosition, toPosition)
        viewModel.moveEmotion(fromPosition, toPosition)
    }

    override fun onItemMoveFinished() {
        viewModel.saveEmotionOrder()
    }
}
