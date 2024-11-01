package com.stopstone.musicplaylist.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.data.model.response.Track
import com.stopstone.musicplaylist.databinding.ItemRecommendationTrackBinding
import com.stopstone.musicplaylist.util.loadImage

class RecommendationAdapter :
    ListAdapter<Track, RecommendationAdapter.RecommendationViewHolder>(BaseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendationViewHolder {
        return RecommendationViewHolder(
            ItemRecommendationTrackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecommendationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class RecommendationViewHolder(
        private val binding: ItemRecommendationTrackBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Track) {
            binding.ivRecommendationTrackCover.loadImage(item.album.images.first().url)
            binding.tvRecommendationTrackTitle.text = item.name
            binding.tvRecommendationTrackArtist.text = item.artists.firstOrNull()?.name
        }
    }
}