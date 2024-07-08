package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.data.model.TrackUiState
import com.stopstone.myapplication.databinding.ItemTrackBinding
import com.stopstone.myapplication.util.loadImage

class TrackAdapter : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {
    private val items = mutableListOf<TrackUiState>()
    private var onItemClickListener: ((TrackUiState) -> Unit)? = null


    fun setOnItemClickListener(listener: (TrackUiState) -> Unit) {
        onItemClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(
            ItemTrackBinding.inflate(
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

    fun submitList(newItems: List<TrackUiState>) {
        val diffCallback = TrackDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val onItemClickListener: ((TrackUiState) -> Unit)?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackUiState) {
            with(binding) {
                track.imageUrl?.let { ivTrackImage.loadImage(it) }
                tvTrackTitle.text = track.title
                tvTrackArtist.text = track.artist

                root.setOnClickListener {
                    onItemClickListener?.invoke(track)
                }
            }
        }
    }
}

class TrackDiffCallback(
    private val oldList: List<TrackUiState>,
    private val newList: List<TrackUiState>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]
}