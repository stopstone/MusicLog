package com.stopstone.myapplication.ui.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stopstone.myapplication.domain.usecase.DeleteAllTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val deleteAllTracksUseCase: DeleteAllTracksUseCase
) : ViewModel() {


    fun clearData() = viewModelScope.launch {
        runCatching {
            deleteAllTracksUseCase()
        }.onSuccess {
            Log.d("SettingViewModel", "Data cleared successfully")
        }.onFailure { e ->
            Log.e("SettingViewModel", "Error clearing data", e)
        }
    }
}