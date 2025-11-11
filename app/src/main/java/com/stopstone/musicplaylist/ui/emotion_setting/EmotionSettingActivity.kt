package com.stopstone.musicplaylist.ui.emotion_setting

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.databinding.ActivityEmotionSettingBinding
import com.stopstone.musicplaylist.ui.emotion_setting.adapter.EmotionAdapter
import com.stopstone.musicplaylist.ui.emotion_setting.adapter.EmotionClickListener
import com.stopstone.musicplaylist.ui.emotion_setting.helper.EmotionItemTouchHelper
import com.stopstone.musicplaylist.ui.emotion_setting.helper.ItemTouchHelperAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EmotionSettingActivity : AppCompatActivity(), EmotionClickListener, ItemTouchHelperAdapter {
    private val binding: ActivityEmotionSettingBinding by lazy {
        ActivityEmotionSettingBinding.inflate(LayoutInflater.from(this))
    }

    private val viewModel: EmotionSettingViewModel by viewModels()

    private val emotionAdapter: EmotionAdapter by lazy {
        EmotionAdapter(this)
    }

    private lateinit var itemTouchHelper: ItemTouchHelper

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
        }
    }

    private fun setupItemTouchHelper() {
        val callback = EmotionItemTouchHelper(this)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvEmotions)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.emotions.collect { emotions ->
                    emotionAdapter.submitList(emotions)
                }
            }
        }
    }


    // EmotionClickListener 구현
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
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
