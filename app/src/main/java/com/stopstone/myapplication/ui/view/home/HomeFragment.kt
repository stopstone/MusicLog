package com.stopstone.myapplication.ui.view.home

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.FragmentHomeBinding
import com.stopstone.myapplication.databinding.ItemTrackBinding
import com.stopstone.myapplication.ui.adapter.CalendarAdapter
import com.stopstone.myapplication.ui.viewmodel.HomeViewModel
import com.stopstone.myapplication.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val adapter: CalendarAdapter by lazy { CalendarAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCalendar()
        setWeekdays()
        setObservers()
        setListeners()
        viewModel.loadTodayTrack()
    }

    private fun setCalendar() {
        binding.calendarContent.rvCalendar.adapter = adapter
        binding.calendarContent.rvCalendar.itemAnimator = null

        val calendar = Calendar.getInstance()
        viewModel.loadCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    private fun setWeekdays() {
        val weekdays =
            resources.getStringArray(R.array.week_days) // string resources 에서 weekday 배열 가져오기

        weekdays.forEach { day ->
            val weekday = TextView(context).apply { // TextView 생성
                text = day
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                setTypeface(null, Typeface.BOLD)
            }
            binding.calendarContent.llWeekDays.addView(weekday) // LinearLayout 에 TextView 추가
        }
    }

    private fun setObservers() {
        viewModel.calendarDates.observe(viewLifecycleOwner) { dates ->
            adapter.submitList(dates.toList())
        }

        viewModel.currentMonth.observe(viewLifecycleOwner) { month ->
            binding.calendarContent.tvCurrentMonth.text = month
        }

        viewModel.todayTrack.observe(viewLifecycleOwner) { dailyTrack ->
            val todayMusic = binding.layoutTodayMusic
            if (dailyTrack != null) {
                val track = dailyTrack.track
                toggleTodayMusicVisibility(true) // 트랙이 있을때

                todayMusic.tvTrackTitle.text = track.name
                todayMusic.tvTrackArtist.text = track.artists.joinToString(", ") { it.name }
                track.album.images.firstOrNull()?.url?.let { todayMusic.ivTrackImage.loadImage(it) }

            } else {
                toggleTodayMusicVisibility(false) // 트랙이 없을때
            }
        }
    }

    private fun setListeners() {
        binding.calendarContent.btnPreviousMonth.setOnClickListener {
            viewModel.previousMonth()
        }

        binding.calendarContent.btnNextMonth.setOnClickListener {
            viewModel.nextMonth()
        }
    }

    private fun toggleTodayMusicVisibility(showTrack: Boolean) {
        binding.layoutTodayMusic.itemTrack.visibility = if (showTrack) View.VISIBLE else View.INVISIBLE
        binding.groupTodayMusicEmpty.visibility = if (showTrack) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}