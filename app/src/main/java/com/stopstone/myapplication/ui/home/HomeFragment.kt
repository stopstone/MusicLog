package com.stopstone.myapplication.ui.home

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.myapplication.R
import com.stopstone.myapplication.data.model.entity.SearchHistory
import com.stopstone.myapplication.databinding.FragmentHomeBinding
import com.stopstone.myapplication.domain.model.CalendarDay
import com.stopstone.myapplication.ui.home.adapter.CalendarAdapter
import com.stopstone.myapplication.ui.home.adapter.RecommendationAdapter
import com.stopstone.myapplication.ui.home.viewmodel.HomeViewModel
import com.stopstone.myapplication.ui.search.adapter.OnItemClickListener
import com.stopstone.myapplication.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val calendarAdapter: CalendarAdapter by lazy { CalendarAdapter(this) }
    private val recommendationAdapter: RecommendationAdapter by lazy { RecommendationAdapter() }

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
        setListeners()
        viewModel.loadTodayTrack()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectTodayTrack() }
                launch { collectCalendarDates() }
                launch { collectCurrentMonth() }
                launch { collectRecommendations() }
            }
        }
    }

    override fun onItemClick(item: Any) {
        when(item) {
            is CalendarDay -> {
                val action = HomeFragmentDirections.actionHomeToTrackDetail(item)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDeleteClick(search: SearchHistory) {
        TODO("Not yet implemented")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCalendar() {
        binding.calendarContent.rvCalendar.adapter = calendarAdapter
        binding.calendarContent.rvCalendar.itemAnimator = null

        val calendar = Calendar.getInstance()
        viewModel.loadCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }

    private fun setWeekdays() {
        val weekdays = resources.getStringArray(R.array.week_days)

        weekdays.forEach { day ->
            val weekday = TextView(context).apply {
                text = day
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                setTypeface(null, Typeface.BOLD)
            }
            binding.calendarContent.llWeekDays.addView(weekday)
        }
    }

    private suspend fun collectCalendarDates() {
        viewModel.calendarDates.collectLatest { dates ->
            calendarAdapter.submitList(dates)
        }
    }

    private suspend fun collectCurrentMonth() {
        viewModel.currentMonth.collectLatest { month ->
            binding.calendarContent.tvCurrentMonth.text = month
        }
    }

    private suspend fun collectTodayTrack() {
        viewModel.todayTrack.collectLatest { dailyTrack ->
            val todayMusic = binding.layoutTodayMusic
            if (dailyTrack != null) {
                val track = dailyTrack.track
                toggleTodayMusicVisibility(true)

                todayMusic.tvTrackTitle.text = track.title
                todayMusic.tvTrackArtist.text = track.artist
                todayMusic.ivTrackImage.loadImage(track.imageUrl)
            } else {
                toggleTodayMusicVisibility(false)
            }
        }
    }

    private suspend fun collectRecommendations() {
        viewModel.recommendations.collect {
            binding.rvRecommendationMusicList.adapter = recommendationAdapter
            recommendationAdapter.submitList(it)
        }
    }


    private fun setListeners() {
        binding.calendarContent.btnPreviousMonth.setOnClickListener {
            viewModel.previousMonth()
        }

        binding.calendarContent.btnNextMonth.setOnClickListener {
            viewModel.nextMonth()
        }

        binding.btnYoutube.setOnClickListener {
            viewModel.todayTrack.value?.let {
                val track = it.track
                openYouTube("${track.title} ${track.artist}")
            }
        }

        binding.btnHomeSettings.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeToSetting()
            findNavController().navigate(action)
        }
    }

    private fun toggleTodayMusicVisibility(showTrack: Boolean) {
        binding.layoutTodayMusic.itemTrack.visibility = if (showTrack) View.VISIBLE else View.INVISIBLE
        binding.groupTodayMusicEmpty.visibility = if (showTrack) View.INVISIBLE else View.VISIBLE
        binding.btnYoutube.visibility = if (showTrack) View.VISIBLE else View.INVISIBLE
    }

    private fun openYouTube(query: String) {
        val encodedQuery = Uri.encode(query)
        val youtubeUri = Uri.parse("vnd.youtube://results?search_query=$encodedQuery")
        val webUri = Uri.parse("https://www.youtube.com/results?search_query=$encodedQuery")

        val youtubeIntent = Intent(Intent.ACTION_VIEW, youtubeUri)
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)

        val chooserIntent = Intent.createChooser(webIntent, "다음 앱으로 열기").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(youtubeIntent))
        }
        startActivity(chooserIntent)
    }
}