package com.stopstone.musicplaylist.ui.emotion_setting.adapter

import android.view.LayoutInflater
import android.view.View
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
            onDragHandleTouch = { holder -> listener.onStartDrag(holder) },
        )
    }

    override fun onBindViewHolder(
        holder: EmotionViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    fun moveItem(
        from: Int,
        to: Int,
    ) {
        val list = currentList.toMutableList()
        val item = list.removeAt(from)
        list.add(to, item)
        submitList(list)
    }

    class EmotionViewHolder(
        private val binding: ItemEmotionBinding,
        private val onDragHandleTouch: (holder: RecyclerView.ViewHolder) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnLongClickListener {
                onDragHandleTouch(this@EmotionViewHolder)
                true
            }
        }

        fun bind(emotion: EmotionUiState) {
            with(binding) {
                tvEmotionName.text = emotion.displayName

                ivDragHandle.setOnLongClickListener {
                    onDragHandleTouch(this@EmotionViewHolder)
                    true
                }
            }
        }
    }
}

interface EmotionClickListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
}
