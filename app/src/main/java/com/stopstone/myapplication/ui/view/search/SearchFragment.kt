package com.stopstone.myapplication.ui.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.stopstone.myapplication.R
import com.stopstone.myapplication.databinding.FragmentSearchBinding
import com.stopstone.myapplication.ui.adapter.TrackAdapter
import com.stopstone.myapplication.ui.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter: TrackAdapter by lazy { TrackAdapter() }
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvSearchTrackList.adapter = adapter
        setSearchButton()
        observeTracks()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSearchButton() {
        binding.btnSearchTrack.setOnClickListener {
            val track = binding.etSearchTrack.text.toString()
            when (track.isNotEmpty()) {
                true -> viewModel.searchTracks(track)
                false -> showToastMessage(getString(R.string.search_empty_message))
            }
        }
    }

    private fun observeTracks() {
        viewModel.tracks.observe(viewLifecycleOwner) { trackList ->
            adapter.submitList(trackList)
        }
    }

    private fun showToastMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}