package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.databinding.TrackItemBinding

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private val items = mutableListOf<Track>()
    private var onItemClickListener: ((Track) -> Unit)? = null


    fun setOnItemClickListener(listener: (Track) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            TrackItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Track>) {
        val diffCallback = TrackDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class TrackViewHolder(
        private val binding: TrackItemBinding,
        private val onItemClickListener: ((Track) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            with(binding) {
                Glide.with(root)
                    .load(track.album.images.firstOrNull()?.url)
                    .centerCrop()
                    .into(ivTrackImage)
                tvTrackTitle.text = track.name
                tvTrackArtist.text = track.artists.joinToString(", ") { it.name }

                root.setOnClickListener {
                    onItemClickListener?.invoke(track)
                }
            }
        }
    }
}

class TrackDiffCallback(
    private val oldList: List<Track>,
    private val newList: List<Track>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].id == newList[newItemPosition].id


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}