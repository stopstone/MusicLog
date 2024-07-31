package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.myapplication.domain.model.TrackUiState
import com.stopstone.myapplication.databinding.ItemTrackBinding
import com.stopstone.myapplication.util.loadImage

class TrackAdapter(
    private val listener: OnItemClickListener?
) : ListAdapter<TrackUiState, TrackAdapter.TrackViewHolder>(TrackDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            ItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClickListener = { position -> listener?.onItemClick(getItem(position)) }
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val onClickListener: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClickListener(adapterPosition)
            }
        }

        fun bind(track: TrackUiState) {
            with(binding) {
                ivTrackImage.loadImage(track.imageUrl)
                tvTrackTitle.text = track.title
                tvTrackArtist.text = track.artist
            }
        }
    }
}

class TrackDiffCallback : DiffUtil.ItemCallback<TrackUiState>() {
    override fun areItemsTheSame(oldItem: TrackUiState, newItem: TrackUiState): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: TrackUiState, newItem: TrackUiState): Boolean {
        return oldItem == newItem
    }
}