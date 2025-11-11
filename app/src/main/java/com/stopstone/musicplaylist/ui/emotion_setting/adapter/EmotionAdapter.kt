package com.stopstone.musicplaylist.ui.emotion_setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.databinding.ItemEmotionBinding
import com.stopstone.musicplaylist.ui.emotion_setting.model.EmotionUiState

class EmotionAdapter(
    private val listener: EmotionClickListener,
) : ListAdapter<EmotionUiState, EmotionAdapter.EmotionViewHolder>(BaseDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): EmotionViewHolder {
        val binding =
            ItemEmotionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return EmotionViewHolder(
            binding,
            onClickListener = { position -> listener.onEmotionClick(getItem(position)) },
        )
    }

    override fun onBindViewHolder(
        holder: EmotionViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class EmotionViewHolder(
        private val binding: ItemEmotionBinding,
        private val onClickListener: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }
        }

        fun bind(emotion: EmotionUiState) {
            with(binding) {
                tvEmotionName.text = emotion.displayName
                cbEmotionSelected.isChecked = emotion.isSelected
            }
        }
    }
}

interface EmotionClickListener {
    fun onEmotionClick(emotion: EmotionUiState)
}
