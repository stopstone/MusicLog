package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.myapplication.BaseDiffCallback
import com.stopstone.myapplication.data.model.SearchHistory
import com.stopstone.myapplication.databinding.ItemSearchHistoryListBinding

class SearchHistoryAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<SearchHistory, SearchHistoryAdapter.SearchHistoryViewHolder>(SearchHistoryDiffCallback()) {

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

class SearchHistoryDiffCallback : BaseDiffCallback<SearchHistory>() {
    override fun getItemId(item: SearchHistory): Any = item.id
    override fun areContentsEqual(oldItem: SearchHistory, newItem: SearchHistory): Boolean = oldItem == newItem
}


interface OnItemClickListener {
    fun onItemClick(item: Any)
    fun onDeleteClick(search: SearchHistory)
}
