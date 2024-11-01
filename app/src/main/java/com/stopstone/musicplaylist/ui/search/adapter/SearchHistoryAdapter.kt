package com.stopstone.musicplaylist.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.databinding.ItemSearchHistoryListBinding

class SearchHistoryAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<SearchHistory, SearchHistoryAdapter.SearchHistoryViewHolder>(
    BaseDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHistoryViewHolder {
        val binding =
            ItemSearchHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchHistoryViewHolder(
            binding,
            onClickListener = { position -> listener.onItemClick(getItem(position)) },
            onDeleteClickListener = { position -> listener.onDeleteClick(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: SearchHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchHistoryViewHolder(
        private val binding: ItemSearchHistoryListBinding,
        private val onClickListener: (position: Int) -> Unit,
        private val onDeleteClickListener: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }

            binding.btnSearchCancel.setOnClickListener {
                onDeleteClickListener(adapterPosition)
            }
        }

        fun bind(search: SearchHistory) {
            binding.tvSearchText.text = search.query
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: Any)
    fun onDeleteClick(search: SearchHistory)
}
