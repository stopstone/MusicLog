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
    private var isDeleteMode: Boolean = false,
) : ListAdapter<EmotionUiState, EmotionAdapter.EmotionViewHolder>(BaseDiffCallback()) {

    fun setDeleteMode(enabled: Boolean) {
        isDeleteMode = enabled
        notifyDataSetChanged()
    }
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
            onItemClick = { position -> listener.onEmotionClick(getItem(position)) },
            isDeleteMode = { isDeleteMode },
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
        private val onItemClick: (position: Int) -> Unit,
        private val isDeleteMode: () -> Boolean,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // 아이템 클릭
            binding.root.setOnClickListener {
                if (isDeleteMode()) {
                    onItemClick(adapterPosition)
                }
            }

            // 아이템 전체 길게 누르기 시 드래그 시작
            binding.root.setOnLongClickListener {
                if (!isDeleteMode()) {
                    onDragHandleTouch(this@EmotionViewHolder)
                }
                true
            }
        }

        fun bind(emotion: EmotionUiState) {
            with(binding) {
                tvEmotionName.text = emotion.displayName

                if (isDeleteMode()) {
                    // 삭제 모드: 체크박스 표시
                    ivDragHandle.visibility = View.GONE
                    cbEmotionDelete.visibility = View.VISIBLE
                    cbEmotionDelete.isChecked = emotion.isSelectedForDelete
                } else {
                    // 일반 모드: 드래그 핸들 표시
                    ivDragHandle.visibility = View.VISIBLE
                    cbEmotionDelete.visibility = View.GONE

                    // 드래그 핸들 길게 누르기
                    ivDragHandle.setOnLongClickListener {
                        onDragHandleTouch(this@EmotionViewHolder)
                        true
                    }
                }
            }
        }
    }
}

interface EmotionClickListener {
    fun onStartDrag(viewHolder: RecyclerView.ViewHolder)

    fun onEmotionClick(emotion: EmotionUiState)
}
