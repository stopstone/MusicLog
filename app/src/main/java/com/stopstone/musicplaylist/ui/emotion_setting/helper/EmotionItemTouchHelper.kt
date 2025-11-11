package com.stopstone.musicplaylist.ui.emotion_setting.helper

import android.graphics.Color
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// Drag & Drop을 처리하는 ItemTouchHelper Callback
class EmotionItemTouchHelper(
    private val adapter: ItemTouchHelperAdapter,
) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(
        viewHolder: RecyclerView.ViewHolder,
        direction: Int,
    ) {
        // Swipe는 사용하지 않음
    }

    override fun isLongPressDragEnabled(): Boolean = false

    override fun isItemViewSwipeEnabled(): Boolean = false

    override fun onSelectedChanged(
        viewHolder: RecyclerView.ViewHolder?,
        actionState: Int,
    ) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.apply {
                alpha = 0.7f
                elevation = 8f
                scaleX = 1.05f
                scaleY = 1.05f
                setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
    ) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.apply {
            alpha = 1.0f
            elevation = 0f
            scaleX = 1.0f
            scaleY = 1.0f
            background = null
        }
        adapter.onItemMoveFinished()
    }
}

interface ItemTouchHelperAdapter {
    fun onItemMove(
        fromPosition: Int,
        toPosition: Int,
    )

    fun onItemMoveFinished()
}

