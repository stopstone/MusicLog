package com.stopstone.musicplaylist.ui.signature_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.databinding.ItemSignatureSongListBinding
import com.stopstone.musicplaylist.ui.signature_list.model.SignatureSongHistoryUiState
import com.stopstone.musicplaylist.util.loadImage

class SignatureSongHistoryAdapter :
    ListAdapter<SignatureSongHistoryUiState, SignatureSongHistoryAdapter.SignatureSongHistoryViewHolder>(
        BaseDiffCallback(),
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SignatureSongHistoryViewHolder {
        val binding =
            ItemSignatureSongListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return SignatureSongHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SignatureSongHistoryViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder that binds a single signature song.
     */
    class SignatureSongHistoryViewHolder(
        private val binding: ItemSignatureSongListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SignatureSongHistoryUiState) {
            binding.tvSignatureSongTitle.text = item.title
            binding.tvSignatureSongArtist.text = item.artist
            binding.tvSignatureSongSince.text = item.selectedAtLabel
            binding.ivSignatureSong.loadImage(item.imageUrl)
        }
    }
}
