package com.stopstone.musicplaylist.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.base.BaseDiffCallback
import com.stopstone.musicplaylist.databinding.ItemCalendarDayBinding
import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.ui.model.TrackUiState
import com.stopstone.musicplaylist.ui.music_search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.util.loadImage

class CalendarAdapter(
    private val listener: OnItemClickListener?,
) : ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(BaseDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): CalendarViewHolder {
        val binding =
            ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return CalendarViewHolder(
            binding,
            onClickListener = { position -> listener?.onItemClick(getItem(position)) },
        )
    }

    override fun onBindViewHolder(
        holder: CalendarViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class CalendarViewHolder(
        private val binding: ItemCalendarDayBinding,
        private val onClickListener: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (binding.ivCalendarAlbumCover.visibility == View.VISIBLE) {
                    onClickListener(adapterPosition)
                }
            }
        }

        fun bind(calendarDay: CalendarDay) {
            when (calendarDay.isToday) {
                true -> binding.root.setBackgroundResource(R.drawable.background_blue300)
                false -> binding.root.background = null
            }

            when {
                calendarDay.id == 0 ->
                    setViewVisibility(
                        dateVisible = false,
                        albumVisible = false,
                    ) // 다른 달인 경우 date, cover 모두 INVISIBLE
                calendarDay.track != null -> showAlbumCover(calendarDay.track)
                else -> showDateText(calendarDay.id) // 노래만 등록 안된 경우 date만 VISIBLE
            }
        }

        private fun setViewVisibility(
            dateVisible: Boolean,
            albumVisible: Boolean,
        ) {
            binding.tvCalendarDate.visibility = if (dateVisible) View.VISIBLE else View.INVISIBLE
            binding.ivCalendarAlbumCover.visibility = if (albumVisible) View.VISIBLE else View.GONE
            binding.root.isClickable = albumVisible
            binding.root.isFocusable = albumVisible
        }

        private fun showAlbumCover(track: TrackUiState) {
            setViewVisibility(dateVisible = false, albumVisible = true)
            binding.ivCalendarAlbumCover.loadImage(track.imageUrl)
        }

        private fun showDateText(day: Int) {
            setViewVisibility(dateVisible = true, albumVisible = false)
            binding.tvCalendarDate.text = day.toString()
        }
    }
}
