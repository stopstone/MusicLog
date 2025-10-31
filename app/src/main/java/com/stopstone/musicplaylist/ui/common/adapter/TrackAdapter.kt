package com.stopstone.musicplaylist.ui.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.databinding.ItemTrackBinding
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.ui.music_search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.util.loadImage

class TrackAdapter(
    private val listener: OnItemClickListener?,
) : ListAdapter<TrackUiState, TrackAdapter.TrackViewHolder>(BaseDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TrackViewHolder =
        TrackViewHolder(
            ItemTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ),
            onClickListener = { position -> listener?.onItemClick(getItem(position)) },
        )

    override fun onBindViewHolder(
        holder: TrackViewHolder,
        position: Int,
    ) {
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
