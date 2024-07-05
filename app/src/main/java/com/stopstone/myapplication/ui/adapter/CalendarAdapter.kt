package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stopstone.myapplication.data.model.CalendarDay
import com.stopstone.myapplication.data.model.Track
import com.stopstone.myapplication.databinding.ItemCalendarDayBinding
import com.stopstone.myapplication.util.loadImage

class CalendarAdapter : ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(CalendarDayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            ItemCalendarDayBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarDay: CalendarDay) {
            when {
                calendarDay.day == 0 -> setViewVisibility( dateVisible = false, albumVisible = false) // 다른 달인 경우 date, cover 모두 INVISIBLE
                calendarDay.track != null -> showAlbumCover(calendarDay.track)
                else -> showDateText(calendarDay.day) // 노래만 등록 안된 경우 date만 VISIBLE
            }
        }

        private fun setViewVisibility(dateVisible: Boolean, albumVisible: Boolean) {
            binding.tvCalendarDate.visibility = if (dateVisible) View.VISIBLE else View.INVISIBLE
            binding.ivCalendarAlbumCover.visibility = if (albumVisible) View.VISIBLE else View.GONE
        }

        private fun showAlbumCover(track: Track) {
            setViewVisibility(dateVisible = false, albumVisible = true)
            track.album.images.firstOrNull()?.url?.let {
                binding.ivCalendarAlbumCover.loadImage(it)
            }
        }

        private fun showDateText(day: Int) {
            setViewVisibility(dateVisible = true, albumVisible = false)
            binding.tvCalendarDate.text = day.toString()
        }
    }
}

private class CalendarDayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
    override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
        return oldItem == newItem
    }
}