package com.stopstone.myapplication.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stopstone.myapplication.data.model.CalendarDay
import com.stopstone.myapplication.databinding.ItemCalendarDayBinding

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
            if (calendarDay.day != 0) {
                if (calendarDay.track != null) {
                    binding.tvCalendarDate.visibility = View.GONE
                    binding.ivCalendarAlbumCover.visibility = View.VISIBLE
                    // 앨범 커버 이미지 로드 (Glide 사용 예시)
                    Glide.with(binding.root.context)
                        .load(calendarDay.track.album.images.firstOrNull()?.url)
                        .into(binding.ivCalendarAlbumCover)
                } else {
                    binding.tvCalendarDate.visibility = View.VISIBLE
                    binding.ivCalendarAlbumCover.visibility = View.GONE
                    binding.tvCalendarDate.text = calendarDay.day.toString()
                }
            } else {
                binding.tvCalendarDate.visibility = View.INVISIBLE
                binding.ivCalendarAlbumCover.visibility = View.GONE
            }
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