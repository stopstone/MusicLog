package com.stopstone.musicplaylist.ui.home

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.databinding.FragmentHomeBinding
import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.ui.detail.TrackDetailActivity
import com.stopstone.musicplaylist.ui.home.adapter.CalendarAdapter
import com.stopstone.musicplaylist.ui.home.adapter.RecommendationAdapter
import com.stopstone.musicplaylist.ui.home.viewmodel.HomeViewModel
import com.stopstone.musicplaylist.ui.search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.ui.setting.SettingActivity
import com.stopstone.musicplaylist.util.loadImage
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

    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    // ActivityResult 등록
    private val trackDetailLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 데이터가 삭제되었을 때 캘린더 새로고침
            viewModel.loadCalendar(currentYear, currentMonth)
            viewModel.loadTodayTrack()
            recommendationAdapter.submitList(emptyList())
            binding.tvRecommendationMusicLabel.visibility = View.GONE
        }
    }

    private val settingLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // 데이터가 전체 삭제되었을 때 캘린더와 오늘의 음악 새로고침
            viewModel.loadCalendar(currentYear, currentMonth)
            viewModel.loadTodayTrack()
            recommendationAdapter.submitList(emptyList())
            binding.tvRecommendationMusicLabel.visibility = View.GONE
        }
    }

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
        when (item) {
            is CalendarDay -> {
                Intent(requireContext(), TrackDetailActivity::class.java)
                    .apply {
                        putExtra("DailyTrack", item)
                        trackDetailLauncher.launch(this)
                    }
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
            TextView(context).apply {
                text = day
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                gravity = Gravity.CENTER
                setPadding(0, 8, 0, 8)
                setTypeface(null, Typeface.BOLD)
                
                binding.calendarContent.llWeekDays.addView(this)
            }
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
            if (it.isEmpty()) {
                binding.tvRecommendationMusicLabel.visibility = View.GONE
            } else {
                binding.tvRecommendationMusicLabel.visibility = View.VISIBLE
                binding.rvRecommendationMusicList.adapter = recommendationAdapter
                recommendationAdapter.submitList(it)
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

        binding.btnYoutube.setOnClickListener {
            viewModel.todayTrack.value?.let {
                val track = it.track
                openYouTube("${track.title} ${track.artist}")
            }
        }

        binding.btnHomeSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            settingLauncher.launch(intent)
        }
    }

    private fun toggleTodayMusicVisibility(showTrack: Boolean) {
        binding.layoutTodayMusic.itemTrack.visibility =
            if (showTrack) View.VISIBLE else View.INVISIBLE
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