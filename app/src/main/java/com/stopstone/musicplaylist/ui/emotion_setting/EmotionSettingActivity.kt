package com.stopstone.musicplaylist.ui.emotion_setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // 키보드가 올라왔을 때만 bottom padding 제거
            val bottomPadding = if (ime.bottom > 0) 0 else systemBars.bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding)

            // 키보드가 올라올 때 텍스트필드를 키보드 위로 이동
            binding.layoutAddEmotion.translationY = -ime.bottom.toFloat()

            // 키보드가 내려갔을 때 텍스트 필드 숨기기
            if (ime.bottom == 0 && binding.layoutAddEmotion.visibility == View.VISIBLE) {
                hideAddEmotionTextField()
            }

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

            // RecyclerView 터치 시 키보드와 텍스트 필드 숨기기
            rvEmotions.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (layoutAddEmotion.visibility == View.VISIBLE) {
                        hideAddEmotionTextField()
                        return@setOnTouchListener true
                    }
                }
                false
            }

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
                showAddEmotionTextField()
            }

            // 텍스트필드 완료 버튼
            etAddEmotion.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    handleAddEmotion()
                    true
                } else {
                    false
                }
            }

            // 완료 버튼 클릭
            btnAddDone.setOnClickListener {
                handleAddEmotion()
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
                        binding.layoutAddEmotion.visibility == View.VISIBLE -> hideAddEmotionTextField()
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

    private fun showAddEmotionTextField() {
        with(binding) {
            layoutAddEmotion.apply {
                visibility = View.VISIBLE
                alpha = 0f
                animate()
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .withEndAction {
                        etAddEmotion.requestFocus()
                        showKeyboard(etAddEmotion)
                    }.start()
            }
        }
    }

    private fun hideAddEmotionTextField() {
        if (binding.layoutAddEmotion.visibility != View.VISIBLE) return
        with(binding) {
            hideKeyboard()
            layoutAddEmotion
                .animate()
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    layoutAddEmotion.visibility = View.GONE
                    etAddEmotion.text?.clear()
                }.start()
        }
    }

    private fun handleAddEmotion() {
        val emotionName =
            binding.etAddEmotion.text
                .toString()
                .trim()
        if (emotionName.isEmpty()) {
            showSnackbar(getString(R.string.message_emotion_name_empty))
        } else {
            val success = viewModel.addCustomEmotion(emotionName)
            if (success) {
                hideAddEmotionTextField()
            } else {
                showSnackbar(getString(R.string.message_emotion_already_exists))
            }
        }
    }

    private fun showKeyboard(view: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view.postDelayed({
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
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
                // 리스트에 하단 패딩 추가하여 삭제 버튼이 리스트를 가리지 않도록 함
                val bottomPadding = resources.getDimensionPixelSize(R.dimen.delete_button_height_with_margin)
                rvEmotions.setPadding(
                    rvEmotions.paddingLeft,
                    rvEmotions.paddingTop,
                    rvEmotions.paddingRight,
                    bottomPadding,
                )
            } else {
                // 일반 모드: 삭제 버튼 숨기고 FAB 표시
                btnDeleteSelected.visibility = View.GONE
                fabMain.visibility = View.VISIBLE
                // 드래그 활성화
                itemTouchHelper.attachToRecyclerView(rvEmotions)
                // 리스트 하단 패딩 제거
                rvEmotions.setPadding(
                    rvEmotions.paddingLeft,
                    rvEmotions.paddingTop,
                    rvEmotions.paddingRight,
                    0,
                )
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
