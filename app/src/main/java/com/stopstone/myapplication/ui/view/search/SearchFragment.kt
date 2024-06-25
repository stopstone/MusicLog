package com.stopstone.myapplication.ui.view.search

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stopstone.myapplication.BuildConfig
import com.stopstone.myapplication.R
import com.stopstone.myapplication.data.api.RetrofitClient
import com.stopstone.myapplication.databinding.FragmentSearchBinding
import com.stopstone.myapplication.ui.adapter.TrackAdapter
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter: TrackAdapter by lazy { TrackAdapter() }

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
        setSearchButton()
        binding.rvSearchTrackList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setSearchButton() {
        binding.btnSearchTrack.setOnClickListener {
            val track = binding.etSearchTrack.text.toString()
            if (track.isNotEmpty()) {
                searchTracks(track)
            } else {
                Toast.makeText(context, getString(R.string.search_empty_message), Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun searchTracks(query: String) = lifecycleScope.launch {
        try {
            val token = getAccessToken()
            val tracks = RetrofitClient.spotifyApi.searchTracks("Bearer $token", query)
            Log.d("SearchFragment", "Tracks: $tracks")
            adapter.submitList(tracks.tracks.items)
        } catch (e: Exception) {
            Log.e("SearchFragment", "Error: ${e.message}")
        }
    }

    private suspend fun getAccessToken(): String {
        val credentials = "${BuildConfig.CLIENT_ID}:${BuildConfig.CLIENT_SECRET}"
        val base64Credentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        val response = RetrofitClient.spotifyAuthApi.getToken(
            "Basic $base64Credentials",
            "client_credentials"
        )
        return response.access_token
    }
}