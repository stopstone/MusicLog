package com.stopstone.musicplaylist.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.stopstone.musicplaylist.R
import com.stopstone.musicplaylist.data.model.entity.SearchHistory
import com.stopstone.musicplaylist.databinding.FragmentHomeBinding
import com.stopstone.musicplaylist.domain.model.CalendarDay
import com.stopstone.musicplaylist.ui.detail.TrackDetailActivity
import com.stopstone.musicplaylist.ui.home.adapter.CalendarAdapter
import com.stopstone.musicplaylist.ui.home.viewmodel.HomeViewModel
import com.stopstone.musicplaylist.ui.music_search.adapter.OnItemClickListener
import com.stopstone.musicplaylist.util.DateUtils
import com.stopstone.musicplaylist.util.EmotionDisplayMapper
import com.stopstone.musicplaylist.util.loadImage
import com.stopstone.musicplaylist.util.setOnClickWithHaptic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment :
    Fragment(),
    OnItemClickListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val calendarAdapter: CalendarAdapter by lazy { CalendarAdapter(this) }

    private var currentYear: Int = DateUtils.getCurrentYear()
    private var currentMonth: Int = DateUtils.getCurrentMonth()

    private lateinit var appContext: Context
    private val emotionTextViews = mutableListOf<TextView>()

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> handleDataRefresh()
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCalendar(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth())
        viewModel.loadTodayTrack()
    }

    override fun onItemClick(item: Any) {
        when (item) {
            is CalendarDay -> {
                Intent(appContext, TrackDetailActivity::class.java).apply {
                    putExtra("DailyTrack", item)
                    activityLauncher.launch(this)
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

        viewModel.loadCalendar(DateUtils.getCurrentYear(), DateUtils.getCurrentMonth())
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
                TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MusicLog_BodyMedium)
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
            if (dailyTrack != null) {
                val track = dailyTrack.track
                toggleTodayMusicVisibility(true)

                binding.tvTrackTitle.text = track.title
                binding.tvTrackArtist.text = track.artist
                binding.ivTrackImage.loadImage(track.imageUrl)
                displayEmotions(dailyTrack.emotions)
            } else {
                toggleTodayMusicVisibility(false)
            }
        }
    }

    private fun displayEmotions(emotions: List<String>) {
        binding.chipGroupHomeEmotions.removeAllViews()
        emotionTextViews.clear()

        if (emotions.isNotEmpty()) {
            val displayList = EmotionDisplayMapper.mapToDisplayNames(appContext, emotions)
            val remainingCount = displayList.size - 2

            // 처음 2개만 표시
            displayList.take(2).forEach { emotionName ->
                val textView = createEmotionTextView(emotionName, showBackground = true)
                binding.chipGroupHomeEmotions.addView(textView)
                emotionTextViews.add(textView)
            }

            // 2개를 넘으면 "+N" 표시
            if (remainingCount > 0) {
                val moreTextView = createEmotionTextView("+$remainingCount", showBackground = false)
                binding.chipGroupHomeEmotions.addView(moreTextView)
                emotionTextViews.add(moreTextView)
            }
        }
    }

    private fun createEmotionTextView(
        emotionName: String,
        showBackground: Boolean = true,
    ) = TextView(context).apply {
        id = View.generateViewId()
        text = emotionName
        if (showBackground) {
            background = AppCompatResources.getDrawable(context, R.drawable.background_gray)
        }
        setPadding(12, 6, 12, 6)
        TextViewCompat.setTextAppearance(this, R.style.TextAppearance_MusicLog_BodySmall)
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

        binding.btnAddMusic.setOnClickWithHaptic {
            val action = HomeFragmentDirections.actionNavigationHomeToNavigationMusicSearch()
            findNavController().navigate(action)
        }
    }

    private fun handleDataRefresh() {
        viewModel.loadCalendar(currentYear, currentMonth)
        viewModel.loadTodayTrack()
    }

    private fun toggleTodayMusicVisibility(showTrack: Boolean) {
        binding.groupHomeTrack.visibility =
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

        val chooserIntent =
            Intent.createChooser(webIntent, "다음 앱으로 열기").apply {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(youtubeIntent))
            }
        startActivity(chooserIntent)
    }
}
