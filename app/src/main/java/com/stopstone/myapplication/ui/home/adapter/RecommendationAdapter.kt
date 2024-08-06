package com.stopstone.myapplication.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.myapplication.base.BaseDiffCallback
import com.stopstone.myapplication.data.model.response.Track
import com.stopstone.myapplication.databinding.ItemRecommendationTrackBinding
import com.stopstone.myapplication.util.loadImage

class RecommendationAdapter: ListAdapter<Track, RecommendationAdapter.RecommendationViewHolder>(BaseDiffCallback()) {

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
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Track) {
            item.album.images.firstOrNull()?.url?.let {
                binding.ivRecommendationTrackCover.loadImage(
                    it
                )
            }
            binding.tvRecommendationTrackTitle.text = item.name
            binding.tvRecommendationTrackArtist.text = item.artists.firstOrNull()?.name
        }
    }
}